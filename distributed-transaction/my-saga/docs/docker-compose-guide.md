# Docker Compose 인프라 구성 가이드

이 문서는 `docker-compose.yml`에 정의된 각 서비스와 설정의 의미를 상세히 해석한다.
전체 컨테이너 구성 요약은 [README - 인프라 구성](../README.md#인프라-구성-docker-compose)을 참고한다.

---

## Zookeeper

### 역할

분산 코디네이션 서비스로서 Kafka 클러스터의 운영을 지원한다.

- **클러스터 메타데이터 관리**: 브로커 목록, 토픽 정보(파티션 수, 복제 팩터 등)를 중앙에서 관리
- **브로커 상태 추적 / 리더 선출**: 브로커의 heartbeat를 모니터링하고, 브로커 장애 시 파티션 리더를 재선출

### 설정 해석

```yaml
zookeeper:
  image: confluentinc/cp-zookeeper:7.5.0
  container_name: saga-zookeeper
  ports:
    - "2181:2181"
  environment:
    ZOOKEEPER_CLIENT_PORT: 2181
    ZOOKEEPER_TICK_TIME: 2000
  volumes:
    - zookeeper-data:/var/lib/zookeeper/data
    - zookeeper-logs:/var/lib/zookeeper/log
  networks:
    - saga-network
```

| 설정 | 값 | 의미 |
|------|----|------|
| `ZOOKEEPER_CLIENT_PORT` | 2181 | 클라이언트(Kafka 브로커)가 Zookeeper에 연결할 때 사용하는 포트 |
| `ZOOKEEPER_TICK_TIME` | 2000 | Zookeeper의 기본 시간 단위(ms). 세션 타임아웃은 `minSessionTimeout = 2 * tickTime`, `maxSessionTimeout = 20 * tickTime`으로 계산된다. 즉, 4초~40초 범위 |

**volumes**:
- `zookeeper-data:/var/lib/zookeeper/data` - 스냅샷 데이터. 컨테이너가 재시작되어도 클러스터 상태가 유지된다.
- `zookeeper-logs:/var/lib/zookeeper/log` - 트랜잭션 로그. Zookeeper의 모든 상태 변경 기록이 보존된다.

---

## Kafka

### 역할

서비스 간 비동기 메시지를 전달하는 메시지 브로커.
Choreography 패턴에서 이벤트(`account.withdraw.success`, `transaction.deposit.success` 등)를 발행/구독하는 핵심 인프라다.

### depends_on

```yaml
depends_on:
  - zookeeper
```

Kafka 브로커는 기동 시 Zookeeper에 접속하여 클러스터에 등록하고 메타데이터를 동기화한다.
Zookeeper가 준비되지 않은 상태에서 Kafka가 시작되면 연결 실패로 브로커가 기동에 실패한다.
`depends_on`은 Zookeeper 컨테이너가 먼저 **시작**되도록 보장한다.

> 참고: `depends_on`은 컨테이너 시작 순서만 보장하며, Zookeeper가 완전히 ready 상태인지는 보장하지 않는다. 운영 환경에서는 `healthcheck` + `condition: service_healthy`를 함께 사용해야 한다.

### 네트워크 리스너 설정

Kafka의 리스너 설정은 "누가 어떤 주소로 접근하는가"를 결정하는 핵심 구성이다.

```
┌─── Docker Network (saga-network) ───┐
│                                      │
│  service_1 ──→ kafka:29092 (PLAINTEXT)│
│  service_2 ──→ kafka:29092           │
│  service_3 ──→ kafka:29092           │
│                                      │
└──────────────────────────────────────┘
         ↕ port forward (9092)
┌─── Host Machine ─────────────────────┐
│  Spring Boot App → localhost:9092     │
│                    (PLAINTEXT_HOST)   │
└──────────────────────────────────────┘
```

현재 프로젝트에서는 Spring Boot 서비스가 Docker 내부가 아닌 **호스트 머신에서 직접 실행**되므로,
호스트에서 접근 가능한 `PLAINTEXT_HOST` 리스너(`localhost:9092`)를 사용한다.

| 설정 | 값 | 의미 |
|------|----|------|
| `KAFKA_LISTENERS` | `PLAINTEXT://0.0.0.0:29092, PLAINTEXT_HOST://0.0.0.0:9092` | 브로커가 실제로 바인딩하는 주소. `0.0.0.0`은 모든 네트워크 인터페이스에서 수신한다는 의미 |
| `KAFKA_ADVERTISED_LISTENERS` | `PLAINTEXT://kafka:29092, PLAINTEXT_HOST://localhost:9092` | 클라이언트에게 "나한테 연결하려면 이 주소를 써라"고 알려주는 값. 초기 연결 후 브로커가 이 주소를 반환하면, 클라이언트는 이후 이 주소로 통신한다 |
| `KAFKA_LISTENER_SECURITY_PROTOCOL_MAP` | `PLAINTEXT:PLAINTEXT, PLAINTEXT_HOST:PLAINTEXT` | 리스너 이름 → 보안 프로토콜 매핑. 두 리스너 모두 암호화 없는 `PLAINTEXT` 프로토콜을 사용 |
| `KAFKA_INTER_BROKER_LISTENER_NAME` | `PLAINTEXT` | 브로커 간 복제(replication) 통신에 사용할 리스너. Docker 내부 네트워크의 `PLAINTEXT` 리스너를 사용한다 |

**ADVERTISED_LISTENERS 상세 설명**:
- `PLAINTEXT://kafka:29092` - Docker 내부 컨테이너 간 통신용. `kafka`는 Docker DNS가 해석하는 컨테이너 이름
- `PLAINTEXT_HOST://localhost:9092` - 호스트 머신에서 접근용. `docker-compose.yml`의 `ports: "9092:9092"`로 포워딩되어 호스트의 `localhost:9092`가 컨테이너의 `9092` 포트로 연결됨

### 로컬 전용 설정 vs 운영 권장 설정

현재 설정은 단일 브로커 로컬 개발 환경에 최적화되어 있다. 운영 환경에서는 아래와 같이 조정해야 한다:

| 설정 | 로컬 (현재) | 운영 권장 | 이유 |
|------|------------|----------|------|
| `KAFKA_BROKER_ID` | 1 | 브로커별 고유 ID | 멀티 브로커 클러스터에서 각 브로커를 식별 |
| `KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR` | 1 | 3 | Consumer group 오프셋 저장 토픽의 복제본. 1이면 브로커 장애 시 오프셋 유실 |
| `KAFKA_TRANSACTION_STATE_LOG_MIN_ISR` | 1 | 2 | 트랜잭션 로그의 최소 동기화 복제본(In-Sync Replica). 이 수만큼 복제가 완료되어야 쓰기 성공으로 간주 |
| `KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR` | 1 | 3 | 트랜잭션 상태 로그의 전체 복제본 수. Saga 패턴처럼 트랜잭션 보장이 중요한 경우 반드시 3 이상으로 설정 |

---

## MySQL

### 역할

**Database per Service 패턴**을 구현한다.
MSA에서 서비스 간 데이터 격리를 위해 각 서비스가 독립적인 데이터베이스를 보유한다.
이를 통해 서비스 간 스키마 결합을 방지하고, 독립적인 배포와 스케일링이 가능해진다.

### 설정 해석 (mysql-service1 기준)

```yaml
mysql-service1:
  image: mysql:8.0
  container_name: saga-mysql-service1
  ports:
    - "3309:3306"
  environment:
    MYSQL_ROOT_PASSWORD: root
    MYSQL_DATABASE: account_db
    MYSQL_USER: account_user
    MYSQL_PASSWORD: account_pass
  volumes:
    - mysql-service1-data:/var/lib/mysql
    - ./datasource/init-service1.sql:/docker-entrypoint-initdb.d/init.sql
  networks:
    - saga-network
```

**ports** (`"3309:3306"`):
- `호스트 포트:컨테이너 내부 포트` 형식
- MySQL은 컨테이너 내부에서 기본 포트 `3306`으로 실행
- 호스트에서 3개의 MySQL 인스턴스가 동시에 동작하므로, 포트 충돌을 피하기 위해 호스트 포트를 3309, 3307, 3308로 분리

| 서비스 | 호스트 포트 | 컨테이너 내부 포트 | 데이터베이스 |
|--------|-----------|-------------------|------------|
| mysql-service1 | 3309 | 3306 | account_db |
| mysql-service2 | 3307 | 3306 | transaction_db |
| mysql-service3 | 3308 | 3306 | notification_db |

**volumes**:
- `mysql-service1-data:/var/lib/mysql` - DB 데이터를 Named Volume에 영속화. 컨테이너를 삭제하고 다시 생성해도 데이터가 유지된다.
- `./datasource/init-service1.sql:/docker-entrypoint-initdb.d/init.sql` - MySQL 공식 이미지의 초기화 메커니즘. `/docker-entrypoint-initdb.d/` 디렉토리에 마운트된 `.sql` 파일은 **컨테이너 최초 시작 시에만** 자동 실행된다. 이미 데이터가 존재하는 볼륨에서는 실행되지 않는다.

---

## Docker Networks

### saga-network (bridge)

```yaml
networks:
  saga-network:
    driver: bridge
```

`bridge`는 Docker의 기본 네트워크 드라이버다.
같은 bridge 네트워크에 속한 컨테이너들은 **컨테이너 이름으로 서로를 DNS 해석**할 수 있다.

예시:
- Kafka 컨테이너에서 `zookeeper:2181`로 접근 가능 (`KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181`)
- 컨테이너 이름 `saga-kafka`가 아닌 서비스 이름 `kafka`로도 해석된다 (Docker Compose가 서비스 이름을 DNS alias로 등록)

이 DNS 해석은 Docker 내장 DNS 서버(127.0.0.11)가 컨테이너 이름 → IP 매핑을 자동으로 관리하여 이루어진다.

### 네트워크 드라이버 비교

| 드라이버 | 설명 | 사용 시나리오 |
|----------|------|-------------|
| **bridge** (현재 사용) | 단일 호스트 내 컨테이너 간 격리된 네트워크 | 로컬 개발, 단일 서버 배포 |
| **host** | 컨테이너가 호스트의 네트워크를 직접 사용 | 네트워크 성능이 중요한 경우. 포트 매핑 불필요하나 격리가 없음 |
| **overlay** | 여러 Docker 호스트에 걸친 네트워크 | Docker Swarm, 멀티노드 클러스터 환경 |

---

## Docker Volumes

### 왜 볼륨이 필요한가

Docker 컨테이너는 기본적으로 **휘발성**이다.
컨테이너를 삭제(`docker-compose down`)하면 내부 파일시스템의 모든 데이터가 사라진다.
데이터베이스나 Zookeeper처럼 상태를 유지해야 하는 서비스에는 **Named Volume**을 사용하여 영속성을 확보한다.

### Named Volume

Docker가 호스트 머신에 저장 위치를 **자동으로 관리**한다.
바인드 마운트(`./path:/container/path`)와 달리, 호스트의 특정 경로에 의존하지 않아 이식성이 좋다.

```bash
# 볼륨 목록 확인
docker volume ls

# 볼륨 상세 정보 (호스트 저장 경로 확인)
docker volume inspect my-saga_mysql-service1-data
```

### 현재 정의된 볼륨

```yaml
volumes:
  zookeeper-data:
  zookeeper-logs:
  kafka-data:
  mysql-service1-data:
  mysql-service2-data:
  mysql-service3-data:
```

| 볼륨 | 마운트 대상 | 용도 |
|------|-----------|------|
| `zookeeper-data` | `/var/lib/zookeeper/data` | Zookeeper 스냅샷 데이터 |
| `zookeeper-logs` | `/var/lib/zookeeper/log` | Zookeeper 트랜잭션 로그 |
| `kafka-data` | (미연결) | 선언만 되어 있고 kafka 서비스의 volumes에 마운트되지 않음 |
| `mysql-service1-data` | `/var/lib/mysql` | account_db 데이터 |
| `mysql-service2-data` | `/var/lib/mysql` | transaction_db 데이터 |
| `mysql-service3-data` | `/var/lib/mysql` | notification_db 데이터 |

> **TODO**: `kafka-data` 볼륨이 하단 `volumes:` 섹션에 선언되어 있으나, `kafka` 서비스의 `volumes`에는 마운트되어 있지 않다. Kafka 로그 데이터 영속화가 필요하다면 `kafka-data:/var/lib/kafka/data`를 추가해야 한다.
