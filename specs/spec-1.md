# Spec 1: 프로젝트 초기 설정 및 도메인 모델 구현

## 1. 명세 및 요구사항 정리

### 1.1 목표
- Spring Boot 프로젝트 초기 설정
- 핵심 도메인 엔티티 설계 및 구현
- 제공 데이터(창고, 상품, 초기 재고) 모델링

### 1.2 제공 데이터
**창고 (3개)**
- 서울 물류센터: 배송비 3,000원, 배송 1일
- 부산 물류센터: 배송비 5,000원, 배송 2일
- 대구 물류센터: 배송비 4,000원, 배송 2일

**상품 (4개)**
- 오버핏 후드: 39,000원
- 슬림 청바지: 59,000원
- 스니커즈: 89,000원
- 크로스백: 45,000원

**초기 재고**
- 서울: 오버핏 후드 50개, 슬림 청바지 30개
- 부산: 오버핏 후드 40개, 스니커즈 20개
- 대구: 슬림 청바지 25개, 크로스백 15개

## 2. 구현할 작업 범위

### 2.1 프로젝트 설정
- Spring Boot 3.5.10 기반 프로젝트 생성
- Java 17, Gradle 8.x 설정
- 필수 의존성 추가
  - Spring Web
  - Spring Data JPA
  - H2 Database (개발용)
  - Lombok
  - Spring Boot Test

### 2.2 도메인 엔티티 구현
- `Warehouse` (창고): id, name, shippingCost, shippingDays
- `Product` (상품): id, name, price
- `Inventory` (재고): id, warehouse, product, quantity
- `Order` (주문): id, orderNumber, status, totalAmount, createdAt
- `OrderItem` (주문 상품): id, order, product, quantity, price
- `Shipment` (배송): id, order, warehouse, shippingCost, shippingDays

### 2.3 초기 데이터 설정
- DataInitializer를 통한 초기 데이터 로딩

## 3. 고려 사항 및 제약 조건

### 3.1 데이터 모델링
- 창고와 상품은 다대다 관계 (Inventory를 통해 연결)
- 주문과 상품은 다대다 관계 (OrderItem을 통해 연결)
- 주문과 창고는 다대다 관계 (Shipment를 통해 연결)
- 주문 상태: PENDING, CONFIRMED, CANCELLED

### 3.2 JPA 설정
- 엔티티 간 연관관계 매핑
- CascadeType, FetchType 적절히 설정
- 낙관적 락(Optimistic Lock) 준비 (@Version)

### 3.3 패키지 구조
```
src/main/java/com/musinsa/order/
├── domain/
│   ├── warehouse/
│   ├── product/
│   ├── inventory/
│   ├── order/
│   └── shipment/
├── repository/
├── service/
├── controller/
└── dto/
```

## 4. 설계 방향

### 4.1 엔티티 설계 원칙
- JPA 표준 어노테이션 활용
- Lombok으로 보일러플레이트 코드 최소화
- 엔티티 내부에 비즈니스 로직 캡슐화
- 불변성 보장 (생성자 패턴, 필요시 Builder)

### 4.2 초기 데이터 로딩 전략
- ApplicationRunner 활용
- 트랜잭션 안전성 보장
- 멱등성 고려 (중복 실행 시에도 안전)

## 5. 테스트 방향

### 5.1 단위 테스트
- 엔티티 생성 및 비즈니스 로직 테스트
- Repository 기본 CRUD 테스트

### 5.2 통합 테스트
- 초기 데이터 로딩 검증
- 연관관계 매핑 검증
