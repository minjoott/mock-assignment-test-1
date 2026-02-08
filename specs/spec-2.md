# Spec 2: 재고 조회 API 구현

## 1. 명세 및 요구사항 정리

### 1.1 기능 요구사항
- 특정 상품의 전체 재고를 조회할 수 있어야 한다
- 어느 창고에 얼마나 있는지 확인할 수 있어야 한다

### 1.2 API 설계
**Endpoint**: `GET /api/inventories/product/{productId}`

**Response**:
```json
{
  "productId": 1,
  "productName": "오버핏 후드",
  "totalQuantity": 90,
  "inventories": [
    {
      "warehouseId": 1,
      "warehouseName": "서울 물류센터",
      "quantity": 50
    },
    {
      "warehouseId": 2,
      "warehouseName": "부산 물류센터",
      "quantity": 40
    }
  ]
}
```

## 2. 구현할 작업 범위

### 2.1 Repository Layer
- `InventoryRepository` 인터페이스 생성
- 상품별 재고 조회 쿼리 메서드 작성
  - `findByProduct_Id(Long productId)`
  - Fetch Join을 통한 N+1 문제 해결

### 2.2 Service Layer
- `InventoryService` 구현
- 상품별 재고 조회 비즈니스 로직
- 전체 재고 합산 로직
- 재고가 없는 경우 예외 처리

### 2.3 Controller Layer
- `InventoryController` 구현
- RESTful API 엔드포인트 제공
- 적절한 HTTP 상태 코드 반환

### 2.4 DTO
- `InventoryResponse`: 재고 조회 응답 DTO
- `WarehouseInventoryDto`: 창고별 재고 정보 DTO

## 3. 고려 사항 및 제약 조건

### 3.1 성능 최적화
- Fetch Join을 통한 N+1 문제 방지
- 필요한 데이터만 조회 (DTO 프로젝션 고려)

### 3.2 예외 처리
- 존재하지 않는 상품 ID 요청 시 적절한 응답
- 재고가 없는 상품 처리

### 3.3 응답 설계
- 창고별 재고 정보 명확히 제공
- 전체 재고 합산 정보 제공

## 4. 설계 방향

### 4.1 Layered Architecture
```
Controller -> Service -> Repository
     ↓           ↓            ↓
   DTO      Business      Entity
```

### 4.2 응답 구조
- 상품 기본 정보 (ID, 이름)
- 전체 재고 수량
- 창고별 상세 재고 리스트

## 5. 테스트 방향

### 5.1 단위 테스트
- InventoryService 로직 테스트
  - 정상 조회
  - 존재하지 않는 상품
  - 재고 합산 로직

### 5.2 통합 테스트
- InventoryController API 테스트
  - 정상 응답 검증
  - 예외 상황 검증
- Repository 쿼리 테스트
  - Fetch Join 동작 검증
