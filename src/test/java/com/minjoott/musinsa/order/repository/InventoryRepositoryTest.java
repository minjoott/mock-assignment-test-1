package com.minjoott.musinsa.order.repository;

import com.minjoott.musinsa.order.domain.entity.Inventory;
import com.minjoott.musinsa.order.domain.entity.Product;
import com.minjoott.musinsa.order.domain.entity.Warehouse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class InventoryRepositoryTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품 ID로 재고를 조회한다 (Warehouse, Product fetch join)")
    void findByProductIdWithWarehouseAndProduct() {
        Warehouse seoul = warehouseRepository.save(new Warehouse("서울 물류센터", 3000, 1));
        Warehouse busan = warehouseRepository.save(new Warehouse("부산 물류센터", 5000, 2));
        Product hoodie = productRepository.save(new Product("오버핏 후드", 39000));

        inventoryRepository.save(new Inventory(seoul, hoodie, 50));
        inventoryRepository.save(new Inventory(busan, hoodie, 40));

        List<Inventory> inventories = inventoryRepository.findByProductIdWithWarehouseAndProduct(hoodie.getId());

        assertThat(inventories).hasSize(2);
        assertThat(inventories)
                .extracting(i -> i.getWarehouse().getName())
                .containsExactlyInAnyOrder("서울 물류센터", "부산 물류센터");
        assertThat(inventories)
                .extracting(Inventory::getQuantity)
                .containsExactlyInAnyOrder(50, 40);
    }

    @Test
    @DisplayName("창고 ID와 상품 ID로 재고를 조회한다")
    void findByWarehouseIdAndProductId() {
        Warehouse seoul = warehouseRepository.save(new Warehouse("서울 물류센터", 3000, 1));
        Product hoodie = productRepository.save(new Product("오버핏 후드", 39000));
        Inventory inventory = inventoryRepository.save(new Inventory(seoul, hoodie, 50));

        Optional<Inventory> found = inventoryRepository.findByWarehouseIdAndProductId(seoul.getId(), hoodie.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getQuantity()).isEqualTo(50);
    }
}
