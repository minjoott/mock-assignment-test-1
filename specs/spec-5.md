# Spec 5: 주문 취소 API 구현

## 1. 명세 및 요구사항 정리

### 1.1 기능 요구사항
- 확정된 주문을 취소할 수 있어야 한다
- 취소 시 재고가 복구되어야 한다

### 1.2 API 설계
**Endpoint**: `DELETE /api/orders/{orderId}`

**Response**:
```json
{
  "orderId": 1,
  "orderNumber": "ORD-20260207-0001",
  "status": "CANCELLED",
  "restoredInventories": [
    {
      "warehouseId": 1,
      "warehouseName": "서울 물류센터",
      "productId": 1,
      "productName": "오버핏 후드",
      "restoredQuantity": 2
    }
  ],
  "cancelledAt": "2026-02-07T11:00:00"
}
```

## 2. 구현할 작업 범위

### 2.1 Service Layer
- `OrderService`에 취소 로직 추가
  - 주문 존재 여부 확인
  - 취소 가능 상태 검증
  - 주문 상태 변경 (CANCELLED)
  - 재고 복구

### 2.2 Controller Layer
- `OrderController`에 취소 엔드포인트 추가

### 2.3 DTO
- `CancelOrderResponse`: 주문 취소 응답 DTO
- `RestoredInventoryDto`: 복구된 재고 정보 DTO

## 3. 고려 사항 및 제약 조건

### 3.1 취소 가능 조건
- 주문이 존재해야 함
- 주문 상태가 CONFIRMED 또는 PENDING이어야 함
- 이미 CANCELLED 상태인 경우 예외 발생

### 3.2 재고 복구
- 주문 시 차감한 창고별 재고를 정확히 복구
- Shipment 정보를 활용하여 어느 창고에서 얼마를 복구할지 결정

### 3.3 트랜잭션 처리
- 주문 취소와 재고 복구가 하나의 트랜잭션
- 실패 시 전체 롤백

### 3.4 멱등성
- 동일한 주문을 여러 번 취소 요청 시 처리 방법
- 이미 취소된 주문 처리

## 4. 설계 방향

### 4.1 취소 처리 흐름
1. 주문 조회
2. 취소 가능 상태 검증
3. 주문 상태 변경 (CANCELLED)
4. Shipment 정보 조회
5. 각 창고별 재고 복구
6. 응답 반환

### 4.2 재고 복구 로직
- Shipment를 통해 어느 창고에서 어떤 상품을 몇 개 보냈는지 확인
- 해당 창고의 재고에 수량 복구

### 4.3 예외 처리
- 주문 없음: `OrderNotFoundException`
- 이미 취소됨: `OrderAlreadyCancelledException`
- 취소 불가 상태: `OrderCannotBeCancelledException`

## 5. 테스트 방향

### 5.1 단위 테스트
- OrderService 취소 로직 테스트
  - 정상 취소 처리
  - 존재하지 않는 주문
  - 이미 취소된 주문
  - 재고 복구 검증

### 5.2 통합 테스트
- OrderController 취소 API 테스트
  - 정상 취소 및 응답 검증
  - 재고 복구 검증 (주문 전후 재고 비교)
  - 예외 상황 검증

### 5.3 시나리오 테스트
- 주문 생성 -> 취소 -> 재고 확인
- 다중 창고 주문 취소 시 모든 창고 재고 복구 검증
