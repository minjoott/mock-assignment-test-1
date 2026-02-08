# Spec 3: 주문 처리 API 구현 - 기본 로직

## 1. 명세 및 요구사항 정리

### 1.1 기능 요구사항
- 고객이 여러 상품을 한 번에 주문할 수 있어야 한다
- 재고가 충분한지 확인해야 한다
- 주문이 확정되면 재고가 차감되어야 한다
- 어느 창고에서 어떤 상품을 발송할지 결정해야 한다

### 1.2 API 설계
**Endpoint**: `POST /api/orders`

**Request**:
```json
{
  "orderItems": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

**Response**:
```json
{
  "orderId": 1,
  "orderNumber": "ORD-20260207-0001",
  "status": "CONFIRMED",
  "totalAmount": 137000,
  "shipments": [
    {
      "warehouseId": 1,
      "warehouseName": "서울 물류센터",
      "items": [
        {
          "productId": 1,
          "productName": "오버핏 후드",
          "quantity": 2
        },
        {
          "productId": 2,
          "productName": "슬림 청바지",
          "quantity": 1
        }
      ],
      "shippingCost": 3000,
      "shippingDays": 1
    }
  ],
  "createdAt": "2026-02-07T10:30:00"
}
```

## 2. 구현할 작업 범위

### 2.1 Repository Layer
- `OrderRepository` 인터페이스 생성
- `OrderItemRepository` 인터페이스 생성
- `ShipmentRepository` 인터페이스 생성
- 주문 번호 생성 로직

### 2.2 Service Layer
- `OrderService` 구현
- 주문 생성 비즈니스 로직
  - 재고 검증
  - 주문 엔티티 생성
  - 재고 차감
  - 배송 정보 생성

### 2.3 Controller Layer
- `OrderController` 구현
- 주문 생성 API 엔드포인트

### 2.4 DTO
- `CreateOrderRequest`: 주문 생성 요청 DTO
- `OrderResponse`: 주문 응답 DTO
- `ShipmentDto`: 배송 정보 DTO

## 3. 고려 사항 및 제약 조건

### 3.1 재고 검증
- 주문한 상품의 전체 재고가 충분한지 확인
- 재고 부족 시 적절한 예외 발생

### 3.2 트랜잭션 처리
- 주문 생성, 재고 차감이 하나의 트랜잭션으로 처리
- 실패 시 전체 롤백

### 3.3 주문 번호 생성
- 유니크한 주문 번호 생성 (예: ORD-YYYYMMDD-XXXX)
- 동시성 고려 (중복 방지)

### 3.4 배송 창고 선택 (간단한 버전)
- 우선 단일 창고에서 모든 상품 배송 시도
- 불가능한 경우 여러 창고로 분산
- (Spec 4에서 최적화 로직 구현)

## 4. 설계 방향

### 4.1 주문 처리 흐름
1. 요청 검증 (상품 존재 여부, 수량 양수)
2. 재고 충분성 검증
3. 주문 엔티티 생성 (PENDING 상태)
4. 배송 창고 할당
5. 재고 차감
6. 주문 상태 변경 (CONFIRMED)
7. 응답 반환

### 4.2 엔티티 연관관계 활용
- Order -> OrderItem (1:N)
- Order -> Shipment (1:N)
- Shipment -> Warehouse (N:1)

## 5. 테스트 방향

### 5.1 단위 테스트
- OrderService 로직 테스트
  - 정상 주문 생성
  - 재고 부족 시 예외
  - 존재하지 않는 상품
  - 재고 차감 검증

### 5.2 통합 테스트
- OrderController API 테스트
  - 정상 주문 생성 및 응답 검증
  - 재고 차감 검증
  - 예외 상황 검증
