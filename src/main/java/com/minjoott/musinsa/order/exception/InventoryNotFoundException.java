package com.minjoott.musinsa.order.exception;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(Long productId) {
        super("재고를 찾을 수 없습니다. productId: " + productId);
    }
}
