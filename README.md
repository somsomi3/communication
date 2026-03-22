# 🏭 산업 데이터 수집 및 실시간 처리 시스템

## 1. 프로젝트 개요
SSAFY특화 프로젝트_자율주행 시뮬레이터 기반 재고관리 서비스 "웰던" 을 고도화한 프로젝트

웰던주소: https://github.com/somsomi3/Well-Done

본 프로젝트는 공장/설비 환경에서 발생하는 다양한 형태의 데이터를
하나의 표준 메시지로 수집하고 안정적으로 저장하는 백엔드 시스템을 구현합니다.

다양한 설비/센서 통신 방식(TCP/UDP/WEBSOCKET/OPC-UA)을 통합해
Kafka 기반 스트림 처리 및 SQL 성능 튜닝까지 진행한 백엔드 시스템입니다.

단순 기능 구현을 넘어 다음을 학습하는 것을 목표로 하였습니다.

- 데이터 흐름 설계
- 비동기 아키텍처 구성
- 메시지 기반 시스템 이해
- SQL 성능 분석 및 인덱스 튜닝

> 📚 7일간 단계적으로 설계·구현한 개발 기록 
> 전체 개발 과정 보기: 

https://happyhappy3.tistory.com/category/%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8


---

### 2. 주요 특징
- 다양한 산업 프로토콜 통합 (TCP / UDP / WebSocket / OPC-UA)
- Kafka를 통한 비동기 메시징 처리
- 로그성 데이터 저장을 고려한 DB 구조 설계
- EXPLAIN ANALYZE 기반 SQL 성능 튜닝 실습


---

## 3. 기술 스택
| 구분        | 기술                                            |
| --------- | --------------------------------------------- |
| Language  | Java 17                                       |
| Framework | Spring Boot                                   |
| Messaging | Apache Kafka                                  |
| Database  | PostgreSQL                                    |
| Cache     | Redis                                         |
| Protocol  | TCP / UDP / WebSocket / OPC-UA (Eclipse Milo) |
| Infra     | Docker / Docker Compose                       |
| DB Tool   | DBeaver                                       |


---

## 4. 전체 아키텍처
```
┌──────────────────────────────┐
│   TCP / UDP / WebSocket      │
│   OPC-UA Client (Milo)       │
└───────────────┬──────────────┘
                ↓
        Kafka Producer
                ↓
        Kafka Topic
      (sensor-log / opcua-data)
                ↓
        Kafka Consumer
                ↓
   ┌────────────┴─────────────┐
   ↓                          ↓
PostgreSQL                Redis (최신값 저장)
(sensor_data table)       (실시간 조회용 캐시)
   ↓
SQL Query / Index Tuning
(EXPLAIN ANALYZE)
```

## 5. 데이터 흐름 설명

1. 다양한 프로토콜을 통해 수집된 데이터를 SensorMessage DTO로 표준화
2. Kafka Producer를 통해 Topic에 메시지 발행
3. Kafka Consumer가 메시지 수신
4. PostgreSQL에 로그성 데이터 저장
5. Redis에 센서 최신값 저장 (실시간 조회용 캐시)
6. EXPLAIN ANALYZE 기반 인덱스 유무 성능 비교 실습

## 6. SQL 성능 튜닝 실습

- 20,000건 샘플 데이터 Insert
- created_at 인덱스 전/후 비교
- Seq Scan → Index Scan 변화 확인
- Execution Time 감소 분석

## 7. 프로젝트 구조

```markdown
📁BE
├─ src
│  ├─ main
│  │  ├─ java
│  │  │  └─ com
│  │  │     └─ be
│  │  │        ├─ config
│  │  │        │  ├─ ObjectMapperConfig.java
│  │  │        │  └─ WebSocketConfig.java
│  │  │        │
│  │  │        ├─ controller
│  │  │        │  └─ KafkaTestController.java
│  │  │        │
│  │  │        ├─ dto
│  │  │        │  └─ SensorMessage.java
│  │  │        │
│  │  │        ├─ kafka
│  │  │        │
│  │  │        │
│  │  │        ├─ opcua
│  │  │        │  └─ OpcUaClientRunner.java
│  │  │        │
│  │  │        ├─ redis
│  │  │        │
│  │  │        ├─ service
│  │  │        │  ├─ KafkaConsumerService.java
│  │  │        │  ├─ KafkaProducerService.java
│  │  │        │  ├─ SensorDataService.java
│  │  │        │  └─ SensorRealtimeService.java
│  │  │        │
│  │  │        ├─ sql
│  │  │        │  ├─ entity
│  │  │        │  │  ├─ SensorDataEntity.java
│  │  │        │  │  └─ Test.java
│  │  │        │  │
│  │  │        │  └─ repository
│  │  │        │     ├─ SensorDataRepository.java
│  │  │        │     └─ TestRepository.java
│  │  │        │
│  │  │        ├─ tcp
│  │  │        │  ├─ TcpClient.java
│  │  │        │  ├─ TcpRunner.java
│  │  │        │  ├─ TcpServer.java
│  │  │        │  └─ TcpWebSocketBridge.java
│  │  │        │
│  │  │        ├─ udp
│  │  │        │  ├─ UdpClient.java
│  │  │        │  ├─ UdpRunner.java
│  │  │        │  └─ UdpServer.java
│  │  │        │
│  │  │        ├─ websocket
│  │  │        │  └─ MessageController.java
│  │  │        │
│  │  │        └─ CommunicationApplication.java
│  │  │
│  │  └─ resources
│  │
│  └─ test
│     └─ java
│        └─ com
│           └─ be
│              └─ CommunicationApplicationTests.java
│
├─ .gitattributes
├─ .gitignore
├─ build.gradle
├─ docker-compose.yml
├─ gradlew
├─ gradlew.bat
├─ HELP.md
├─ README.md
└─ settings.gradle
```

## 8. 실행 방법
```
docker-compose up -d
./gradlew bootRun
```

## 9. 각단계별 상세 정리 블로그
Day1 프로젝트 기획 및 기본설정
https://happyhappy3.tistory.com/143

Day2 Tcp/ Udp
https://happyhappy3.tistory.com/151

Day3 WebSocket
https://happyhappy3.tistory.com/157

Day4 Kafka
https://happyhappy3.tistory.com/159

Day5 OPC-UA Reader
https://happyhappy3.tistory.com/160

Day6 SQL 튜닝실습
https://happyhappy3.tistory.com/161
