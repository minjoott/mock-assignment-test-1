package com.minjoott.musinsa.order.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class InventoryTest {

    @Test
    @DisplayName("재고를 성공적으로 차감한다")
    void deductInventorySuccessfully() {
        Warehouse warehouse = new Warehouse("서울 물류센터", 3000, 1);
        Product product = new Product("오버핏 후드", 39000);
        Inventory inventory = new Inventory(warehouse, product, 50);

        inventory.deduct(10);

        assertThat(inventory.getQuantity()).isEqualTo(40);
    }

    @Test
    @DisplayName("재고가 부족하면 예외가 발생한다")
    void throwsExceptionWhenInsufficientInventory() {
        Warehouse warehouse = new Warehouse("서울 물류센터", 3000, 1);
        Product product = new Product("오버핏 후드", 39000);
        Inventory inventory = new Inventory(warehouse, product, 5);

        assertThatThrownBy(() -> inventory.deduct(10))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("재고가 부족합니다.");
    }

    @Test
    @DisplayName("재고를 성공적으로 복구한다")
    void restoreInventorySuccessfully() {
        Warehouse warehouse = new Warehouse("서울 물류센터", 3000, 1);
        Product product = new Product("오버핏 후드", 39000);
        Inventory inventory = new Inventory(warehouse, product, 40);

        inventory.restore(10);

        assertThat(inventory.getQuantity()).isEqualTo(50);
    }
}
