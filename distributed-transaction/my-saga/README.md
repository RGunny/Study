# Saga Pattern - 분산 트랜잭션 학습 프로젝트

MSA 환경에서 분산 트랜잭션을 처리하기 위한 Saga 패턴 학습 프로젝트.
**계좌 이체** 시나리오를 통해 Orchestration과 Choreography 두 가지 방식을 **구조적으로 분리**하여 비교한다.

## 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Kotlin 2.1.20, Java 21 |
| Framework | Spring Boot 4.0.1 |
| ORM | Spring Data JPA |
| Messaging | Apache Kafka (Confluent 7.4.0), Zookeeper |
| Database | MySQL 8.0 (서비스별 독립 DB) |
| Build | Gradle Kotlin DSL |
| Infra | Docker Compose |

---

## 아키텍처 개요

4개의 독립적인 마이크로서비스가 각자 전용 데이터베이스를 보유한다 (Database Per Service).
Orchestration과 Choreography가 **별도 서비스로 분리**되어 두 패턴의 차이를 명확하게 보여준다.

```
[Client]
   |
   +-- POST /api/orchestration/transfer
   |         |
   |   [saga-coordinator :8084]     <-- SagaState + REST 호출로 흐름 제어
   |     +-> account-service /internal/withdraw
   |     +-> transaction-service /internal/deposit
   |     +-> notification-service /internal/notification
   |
   +-- POST /api/choreography/transfer
             |
       [account-service :8081]      <-- SagaState 없이 출금 + 이벤트 발행
             | Kafka
       [transaction-service :8082]  --> [notification-service :8083]
             | Kafka (실패 시)
       [account-service :8081]      <-- 이벤트 정보만으로 보상
```

---

## 모듈 구조

```
my-saga/
+-- build.gradle.kts          # 루트 빌드 설정 (Kotlin 2.1.20)
+-- settings.gradle.kts       # 멀티모듈 등록 (5개 모듈)
+-- docker-compose.yml        # Kafka, Zookeeper, MySQL x4
+-- datasource/
|   +-- init-service1.sql     # account_db 스키마 + 시드 데이터
|   +-- init-service2.sql     # transaction_db 스키마
|   +-- init-service3.sql     # notification_db 스키마
|   +-- init-coordinator.sql  # coordinator_db 스키마 (saga_state)
|
+-- common/                   # 공유 라이브러리
|   +-- dto/                  # TransferDto, DepositDto, NotificationDto, WithdrawDto
|   +-- event/                # Kafka 이벤트 (Withdraw/Deposit/Notification Success/Failed)
|
+-- saga-coordinator/         # Saga Coordinator (:8084) - Orchestration 전용
|   +-- api/                  #   TransferController - orchestration 진입점
|   +-- service/              #   OrchestrationService - REST 기반 중앙 조정자
|   +-- domain/               #   SagaState - Saga 상태 추적
|   +-- repository/
|
+-- account-service/          # Account Service (:8081) - 순수 계좌 도메인
|   +-- api/
|   |   +-- TransferController    # choreography 진입점
|   |   +-- WithdrawController    # 내부 API (/internal/withdraw, /internal/withdraw/compensate)
|   +-- service/
|   |   +-- AccountService        # 출금/보상 도메인 로직
|   |   +-- ChoreographyService   # 순수 Choreography (SagaState 없음)
|   +-- domain/               #   Account, AccountTransaction
|   +-- repository/
|
+-- transaction-service/      # Transaction Service (:8082)
|   +-- controller/           #   DepositController - 내부 API (/internal/deposit)
|   +-- service/              #   DepositService - 입금 처리 + Kafka 이벤트 발행
|   +-- domain/               #   Transaction, Deposit
|   +-- repository/
|
+-- notification-service/     # Notification Service (:8083)
    +-- controller/           #   NotificationController - 내부 API (/internal/notification)
    +-- service/              #   NotificationService - 알림 저장 + Kafka 소비
    +-- domain/               #   Notification
    +-- repository/
```

---

## Saga 패턴

### Orchestration (동기식 REST)

**saga-coordinator**가 중앙 조정자로서 RestTemplate을 통해 각 서비스를 순차 호출한다.
SagaState로 진행 상태를 명시적으로 추적한다.

```
Client
  |
  |  POST /api/orchestration/transfer
  v
Saga Coordinator (:8084)
  |
  |  1. REST POST -> Account Service /internal/withdraw
  |     출금 처리 (fromAccount.balance 차감)
  |     SagaState 생성 (status=STARTED)
  |
  |  2. REST POST -> Transaction Service /internal/deposit
  |     +-- 성공: Deposit 저장, Transaction 저장
  |     +-- 실패: REST POST -> Account Service /internal/withdraw/compensate
  |
  |  3. REST POST -> Notification Service /internal/notification
  |     +-- 성공: 알림 저장
  |     +-- 실패: 무시 (best-effort)
  |
  v
SagaState.status = COMPLETED
```

**보상 트랜잭션 (Compensation)**

Step 2에서 입금이 실패하면 Coordinator가 Account Service에 보상을 요청한다:

```
입금 실패 감지
  |
  |  REST -> Account Service /internal/withdraw/compensate
  |  fromAccount.balance += amount (잔액 복원)
  |  sagaState.status = COMPENSATED
  v
이체 실패 응답 반환
```

---

### Choreography (비동기 Kafka)

중앙 조정자 없이 각 서비스가 **이벤트를 발행/구독**하여 서로를 트리거한다.
**SagaState가 존재하지 않으며**, 이벤트에 담긴 정보만으로 보상을 처리한다 (순수 Choreography).

```
Client
  |
  |  POST /api/choreography/transfer
  v
Account Service (:8081)
  |  출금 처리 (SagaState 없음, 이벤트만 발행)
  |
  |  PUBLISH -> [account.withdraw.success]
  v
Transaction Service (:8082)
  |  CONSUME <- [account.withdraw.success]
  |  Transaction 저장 + Deposit 저장
  |
  |  +-- 성공: PUBLISH -> [transaction.deposit.success]
  |  +-- 실패: PUBLISH -> [transaction.deposit.failed]
  |              (이벤트에 amount, fromAccountNumber 포함)
  v
+------------------------------+------------------------------+
|                              |                              |
v                              v                              v
Account Service           Account Service             Notification Service
CONSUME                   CONSUME                     CONSUME
[deposit.success]         [deposit.failed]            [deposit.success]
로그 출력 (Saga 암묵적 완료)  compensateWithdraw()        알림 저장
                          (이벤트 정보만으로 잔액 복원)
```

**Kafka 토픽 목록**

| 토픽 | 발행 | 구독 | 이벤트 | 용도 |
|------|------|------|--------|------|
| `account.withdraw.success` | account-service | transaction-service | WithdrawSuccessEvent | 출금 성공 -> 입금 트리거 |
| `account.withdraw.failed` | account-service | notification-service | WithdrawFailedEvent | 출금 실패 -> 실패 알림 |
| `transaction.deposit.success` | transaction-service | account-service, notification-service | DepositSuccessEvent | 입금 성공 -> Saga 완료 + 알림 |
| `transaction.deposit.failed` | transaction-service | account-service | DepositFailedEvent | 입금 실패 -> 보상 트랜잭션 |
| `notification.failed` | notification-service | account-service | NotificationFailedEvent | 알림 실패 로그 |

---

### 두 패턴 비교

| 항목 | Orchestration | Choreography |
|------|---------------|--------------|
| 구현 서비스 | saga-coordinator | account-service |
| 상태 관리 | SagaState 엔티티로 명시적 추적 | 없음 (이벤트 흐름으로 암묵적) |
| 통신 방식 | 동기 REST (RestTemplate) | 비동기 Kafka 이벤트 |
| 흐름 제어 | 중앙 조정자가 순차 호출 | 각 서비스가 이벤트로 트리거 |
| 결합도 | 조정자가 모든 서비스를 알아야 함 | 서비스 간 직접 의존 없음 (토픽만 공유) |
| 트랜잭션 가시성 | 한 곳에서 전체 흐름 파악 가능 | 이벤트 추적이 필요하여 흐름 파악이 어려움 |
| 보상 처리 | try-catch로 즉시 보상 | 실패 이벤트 구독하여 비동기 보상 |
| 보상 데이터 | SagaState에서 조회 | 이벤트에 포함된 정보 사용 |
| 장애 전파 | 하나가 실패하면 즉시 감지 | 이벤트 유실 시 감지가 어려움 |
| 확장성 | 동기 호출로 인한 병목 가능 | 비동기 처리로 높은 확장성 |

---

## 데이터베이스 스키마

### account_db (account-service, 포트 3309)

| 테이블 | 설명 | 주요 컬럼 |
|--------|------|-----------|
| `accounts` | 계좌 마스터 | account_number, balance, status |
| `account_transactions` | 출금 이력 | account_id, amount, transaction_type, saga_id, status |

초기 시드 데이터:

| 계좌번호 | 잔액 |
|----------|------|
| 1000-0001 | 1,000,000 |
| 1000-0002 | 500,000 |
| 1000-0003 | 2,000,000 |

### coordinator_db (saga-coordinator, 포트 3310)

| 테이블 | 설명 | 주요 컬럼 |
|--------|------|-----------|
| `saga_state` | Saga 상태 추적 (Orchestration 전용) | from_account_number, to_account_number, amount, status |

### transaction_db (transaction-service, 포트 3307)

| 테이블 | 설명 | 주요 컬럼 |
|--------|------|-----------|
| `transactions` | 이체 트랜잭션 내역 | saga_id, from/to_account_number, amount, status |
| `deposits` | 입금 상세 내역 | transaction_id, account_number, amount, saga_id, status |

### notification_db (notification-service, 포트 3308)

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
| saga-mysql-coordinator | mysql:8.0 | 3310 | coordinator_db |

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

Kafka, Zookeeper, MySQL 4개가 기동된다. 각 MySQL은 `datasource/init-*.sql`로 자동 초기화된다.

### 2. 프로젝트 빌드

```bash
./gradlew clean build
```

### 3. 서비스 실행

터미널 4개에서 각각 실행한다:

```bash
# Account Service (포트 8081) - Choreography + 내부 출금 API
./gradlew :account-service:bootRun

# Transaction Service (포트 8082)
./gradlew :transaction-service:bootRun

# Notification Service (포트 8083)
./gradlew :notification-service:bootRun

# Saga Coordinator (포트 8084) - Orchestration 전용
./gradlew :saga-coordinator:bootRun
```

---

## API 사용 예시

### Orchestration 방식 이체 (saga-coordinator 경유)

```bash
curl -X POST http://localhost:8084/api/orchestration/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "fromAccountNumber": "1000-0001",
    "toAccountNumber": "1000-0002",
    "amount": 100000
  }'
```

### Choreography 방식 이체 (account-service 직접)

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
  "message": "Transfer successful"
}
```
