# Saga Pattern - 분산 트랜잭션 학습 프로젝트

MSA 환경에서 분산 트랜잭션을 처리하기 위한 Saga 패턴 학습 프로젝트.
**계좌 이체** 시나리오를 통해 Orchestration과 Choreography 두 가지 방식을 나란히 구현하여 비교한다.

## 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Kotlin 1.9.20, Java 17 |
| Framework | Spring Boot 3.2.0 |
| ORM | Spring Data JPA |
| Messaging | Apache Kafka (Confluent 7.4.0), Zookeeper |
| Database | MySQL 8.0 (서비스별 독립 DB) |
| Build | Gradle Kotlin DSL |
| Infra | Docker Compose |

---

## 아키텍처 개요

3개의 독립적인 마이크로서비스가 각자 전용 데이터베이스를 보유한다 (Database Per Service).

```
                         +---------------------+
                         |      Client         |
                         +----------+----------+
                                    |
                          POST /api/*/transfer
                                    |
                                    v
+----------------------------------------------------------------------+
|                   Account Service (service_1, :8081)                  |
|                                                                      |
|  - 출금 처리 (accounts, account_transactions)                         |
|  - Saga 상태 관리 (saga_state)                                        |
|  - Orchestration: 중앙 오케스트레이터 역할                              |
|  - Choreography: 이벤트 발행 및 보상 처리                               |
+------+---------------------------+-----------------------------------+
       |                           |
       | REST (Orchestration)      | Kafka (Choreography)
       |                           |
       v                           v
+--------------------+    +--------------------+
| Transaction Svc    |    |    Kafka Broker     |
| (service_2, :8082) |    |      (:9092)        |
|                    |    +----+----------+-----+
| - 입금 처리         |         |          |
| - 트랜잭션 기록      |         v          v
+--------------------+  service_2     service_3
                        (consume)     (consume)
+--------------------+
| Notification Svc   |
| (service_3, :8083) |
|                    |
| - 알림 저장/발송     |
+--------------------+
```

---

## 모듈 구조

```
my-saga/
├── build.gradle.kts          # 루트 빌드 설정 (Kotlin 1.9.20)
├── settings.gradle.kts       # 멀티모듈 등록
├── docker-compose.yml        # Kafka, Zookeeper, MySQL x3
├── datasource/
│   ├── init-service1.sql     # account_db 스키마 + 시드 데이터
│   ├── init-service2.sql     # transaction_db 스키마
│   └── init-service3.sql     # notification_db 스키마
│
├── common/                   # 공유 라이브러리
│   └── dto/                  # TransferDto, DepositDto, NotificationDto
│   └── event/                # Kafka 이벤트 클래스 (WithdrawSuccess/Failed, DepositSuccess/Failed)
│
├── service_1/                # Account Service (:8081)
│   ├── controller/           #   TransferController - 외부 API 진입점
│   ├── service/
│   │   ├── OrchestrationService.kt   # REST 기반 Saga 오케스트레이터
│   │   └── ChoreographyService.kt    # Kafka 이벤트 기반 Saga
│   ├── domain/               #   Account, AccountTransaction, SagaState
│   └── repository/
│
├── service_2/                # Transaction Service (:8082)
│   ├── controller/           #   DepositController - 내부 API (/internal/deposit)
│   ├── service/              #   DepositService - 입금 처리 + Kafka 이벤트 발행
│   ├── domain/               #   Transaction, Deposit
│   └── repository/
│
└── service_3/                # Notification Service (:8083)
    ├── controller/           #   NotificationController - 내부 API (/internal/notification)
    ├── service/              #   NotificationService - 알림 저장 + Kafka 소비
    ├── domain/               #   Notification
    └── repository/
```

---

## Saga 패턴

### Orchestration (동기식 REST)

Account Service가 **중앙 오케스트레이터**로서 RestTemplate을 통해 다른 서비스를 순차 호출한다.

```
Client
  |
  |  POST /api/orchestration/transfer
  v
Account Service (Orchestrator)
  |
  |  1. 출금 처리 (fromAccount.balance 차감)
  |     AccountTransaction 저장 (status=COMPLETED)
  |     SagaState 생성 (status=STARTED)
  |
  |  2. REST POST -> Transaction Service /internal/deposit
  |     +-- 성공: Deposit 저장, Transaction 저장
  |     +-- 실패: 보상 트랜잭션 실행 (아래 참조)
  |
  |  3. REST POST -> Notification Service /internal/notification
  |     +-- 성공: 알림 저장
  |     +-- 실패: 무시 (best-effort)
  |
  v
SagaState.status = COMPLETED
```

**보상 트랜잭션 (Compensation)**

Step 2에서 입금 처리가 실패하면 출금을 원복한다:

```
입금 실패 감지
  |
  |  fromAccount.balance += amount    (잔액 복원)
  |  withdrawTx.status = COMPENSATED
  |  sagaState.status = COMPENSATED
  v
이체 실패 응답 반환
```

Notification 실패는 비핵심 작업이므로 보상하지 않는다.

---

### Choreography (비동기 Kafka)

중앙 오케스트레이터 없이 각 서비스가 **이벤트를 발행/구독**하여 서로를 트리거한다.

```
Client
  |
  |  POST /api/choreography/transfer
  v
Account Service (service_1)
  |  출금 처리 + SagaState 생성 (patternType=CHOREOGRAPHY)
  |
  |  PUBLISH -> [account.withdraw.success]
  v
Transaction Service (service_2)
  |  CONSUME <- [account.withdraw.success]
  |  Transaction 저장 + Deposit 저장
  |
  |  +-- 성공: PUBLISH -> [transaction.deposit.success]
  |  +-- 실패: PUBLISH -> [transaction.deposit.failed]
  v
+---------------------------------+---------------------------------+
|                                 |                                 |
v                                 v                                 v
Account Service (service_1)   Account Service (service_1)    Notification Service (service_3)
CONSUME                       CONSUME                        CONSUME
[transaction.deposit.success] [transaction.deposit.failed]   [transaction.deposit.success]
                                                             [account.withdraw.failed]
sagaState = COMPLETED         compensateWithdraw()           알림 저장
                              (잔액 복원, sagaState =
                               COMPENSATED)
```

**Kafka 토픽 목록**

| 토픽 | 발행 | 구독 | 이벤트 | 용도 |
|------|------|------|--------|------|
| `account.withdraw.success` | service_1 | service_2 | WithdrawSuccessEvent | 출금 성공 -> 입금 트리거 |
| `account.withdraw.failed` | service_1 | service_3 | WithdrawFailedEvent | 출금 실패 -> 실패 알림 |
| `transaction.deposit.success` | service_2 | service_1, service_3 | DepositSuccessEvent | 입금 성공 -> Saga 완료 + 알림 |
| `transaction.deposit.failed` | service_2 | service_1 | DepositFailedEvent | 입금 실패 -> 보상 트랜잭션 |

---

### 두 패턴 비교

| 항목 | Orchestration | Choreography |
|------|---------------|--------------|
| 통신 방식 | 동기 REST (RestTemplate) | 비동기 Kafka 이벤트 |
| 흐름 제어 | 중앙 오케스트레이터가 순차 호출 | 각 서비스가 이벤트로 트리거 |
| 결합도 | 오케스트레이터가 모든 서비스를 알아야 함 | 서비스 간 직접 의존 없음 (토픽만 공유) |
| 트랜잭션 가시성 | 한 곳에서 전체 흐름 파악 가능 | 이벤트 추적이 필요하여 흐름 파악이 어려움 |
| 보상 처리 | try-catch로 즉시 보상 | 실패 이벤트 구독하여 비동기 보상 |
| 장애 전파 | 하나가 실패하면 즉시 감지 | 이벤트 유실 시 감지가 어려움 |
| 확장성 | 동기 호출로 인한 병목 가능 | 비동기 처리로 높은 확장성 |
| 적합한 경우 | 단순한 흐름, 적은 수의 서비스 | 복잡한 흐름, 많은 서비스 참여 |

---

## 데이터베이스 스키마

### account_db (service_1, 포트 3309)

| 테이블 | 설명 | 주요 컬럼 |
|--------|------|-----------|
| `accounts` | 계좌 마스터 | account_number, balance, status |
| `account_transactions` | 출금 이력 | account_id, amount, transaction_type, saga_id, status |
| `saga_state` | Saga 상태 추적 | pattern_type, from_account_id, to_account_id, amount, status |

초기 시드 데이터:

| 계좌번호 | 잔액 |
|----------|------|
| 1000-0001 | 1,000,000 |
| 1000-0002 | 500,000 |
| 1000-0003 | 2,000,000 |

### transaction_db (service_2, 포트 3307)

| 테이블 | 설명 | 주요 컬럼 |
|--------|------|-----------|
| `transactions` | 이체 트랜잭션 내역 | saga_id, from/to_account_number, amount, status |
| `deposits` | 입금 상세 내역 | transaction_id, account_number, amount, saga_id, status |

### notification_db (service_3, 포트 3308)

| 테이블 | 설명 | 주요 컬럼 |
|--------|------|-----------|
| `notifications` | 알림 내역 | user_id, saga_id, notification_type, message, status |

---

## 인프라 구성 (Docker Compose)

| 컨테이너 | 이미지 | 포트 | 역할 |
|----------|--------|------|------|
| saga-zookeeper | confluentinc/cp-zookeeper:7.5.0 | 2181 | Kafka 코디네이터 |
| saga-kafka | confluentinc/cp-kafka:7.4.0 | 9092 | 메시지 브로커 |
| saga-mysql-service1 | mysql:8.0 | 3309 | account_db |
| saga-mysql-service2 | mysql:8.0 | 3307 | transaction_db |
| saga-mysql-service3 | mysql:8.0 | 3308 | notification_db |

모든 컨테이너는 `saga-network` (bridge) 네트워크로 연결된다.
Spring Boot 서비스는 Docker에 포함되지 않으며 로컬에서 직접 실행한다.

> 각 컨테이너의 설정 상세 해석은 [Docker Compose 인프라 가이드](docs/docker-compose-guide.md)를 참고한다.

---

## 실행 방법

### 1. 인프라 시작

```bash
cd my-saga
docker-compose up -d
```

Kafka, Zookeeper, MySQL 3개가 기동된다. 각 MySQL은 `datasource/init-*.sql`로 자동 초기화된다.

### 2. 프로젝트 빌드

```bash
./gradlew clean build
```

### 3. 서비스 실행

터미널 3개에서 각각 실행한다:

```bash
# Account Service (포트 8081)
./gradlew :service_1:bootRun

# Transaction Service (포트 8082)
./gradlew :service_2:bootRun

# Notification Service (포트 8083)
./gradlew :service_3:bootRun
```

---

## API 사용 예시

### Orchestration 방식 이체

```bash
curl -X POST http://localhost:8081/api/orchestration/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountNumber": "1000-0001",
    "toAccountNumber": "1000-0002",
    "amount": 100000
  }'
```

### Choreography 방식 이체

```bash
curl -X POST http://localhost:8081/api/choreography/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountNumber": "1000-0001",
    "toAccountNumber": "1000-0002",
    "amount": 100000
  }'
```

### 응답 예시

```json
{
  "sagaId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "COMPLETED",
  "message": "Transfer completed successfully"
}
```
