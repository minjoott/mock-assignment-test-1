# Spec 6: 동시성 제어 구현

## 1. 명세 및 요구사항 정리

### 1.1 비기능 요구사항
- 여러 고객이 동시에 같은 상품을 주문할 수 있다
- 재고보다 많이 팔리는 일이 없어야 한다

### 1.2 동시성 문제 시나리오
**Race Condition 예시**:
```
시간 T1: 고객 A가 상품 X (재고 10개) 중 8개 주문 시작
시간 T2: 고객 B가 상품 X (재고 10개) 중 5개 주문 시작
시간 T3: 고객 A 주문 완료 (재고 2개 남음)
시간 T4: 고객 B 주문 완료 (재고 -3개?? 문제 발생!)
```

## 2. 구현할 작업 범위

### 2.1 락(Lock) 전략 선택
**고려 대상**:
- 비관적 락 (Pessimistic Lock)
- 낙관적 락 (Optimistic Lock)
- 분산 락 (Distributed Lock)

### 2.2 동시성 제어 구현
- `Inventory` 엔티티에 동시성 제어 추가
- 재고 차감/복구 시 원자성 보장
- 재고 부족 시 적절한 예외 처리

### 2.3 Service Layer 개선
- `InventoryService` 동시성 안전 로직 추가
- 트랜잭션 격리 수준 검토

## 3. 고려 사항 및 제약 조건

### 3.1 락 전략 비교

**비관적 락 (Pessimistic Lock)**:
- 장점: 충돌 발생 시 데이터 정합성 확실히 보장
- 단점: 성능 저하 가능, 데드락 위험
- 적용: `@Lock(LockModeType.PESSIMISTIC_WRITE)`

**낙관적 락 (Optimistic Lock)**:
- 장점: 성능이 좋음, 읽기 위주 작업에 유리
- 단점: 충돌 발생 시 재시도 필요, 복잡도 증가
- 적용: `@Version` 어노테이션

**본 프로젝트 선택 기준**:
- 재고는 쓰기 작업이 빈번함
- 충돌 시 재시도보다 확실한 보장이 중요
- **비관적 락 선택**

### 3.2 트랜잭션 설정
- 격리 수준: READ_COMMITTED 이상
- 트랜잭션 범위: 재고 조회 ~ 차감까지 포함
- 타임아웃 설정

### 3.3 성능 최적화
- 락 범위 최소화
- 락 보유 시간 최소화
- 인덱스 활용

## 4. 설계 방향

### 4.1 비관적 락 적용
```java
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.product.id = :productId")
    List<Inventory> findByProductIdWithLock(@Param("productId") Long productId);
}
```

### 4.2 재고 차감 로직
```java
@Transactional
public void deductInventory(Long warehouseId, Long productId, int quantity) {
    Inventory inventory = findWithLock(warehouseId, productId);
    
    if (inventory.getQuantity() < quantity) {
        throw new InsufficientInventoryException();
    }
    
    inventory.deduct(quantity);
}
```

### 4.3 예외 처리
- `InsufficientInventoryException`: 재고 부족
- `InventoryNotFoundException`: 재고 없음
- 락 타임아웃 처리

## 5. 테스트 방향

### 5.1 동시성 테스트
- 멀티 스레드 환경 테스트
  - ExecutorService 활용
  - CountDownLatch로 동시 실행
  - 여러 스레드가 동일 상품 주문
  - 최종 재고 검증

### 5.2 시나리오 테스트
**케이스 1**: 재고 10개, 10명이 1개씩 주문
- 결과: 10명 성공, 재고 0개

**케이스 2**: 재고 10개, 20명이 1개씩 주문
- 결과: 10명 성공, 10명 실패, 재고 0개

**케이스 3**: 재고 10개, 5명이 3개씩 주문
- 결과: 3명 성공, 2명 실패, 재고 1개

### 5.3 성능 테스트
- 동시 요청 수 증가 시 응답 시간 측정
- 데드락 발생 여부 확인
