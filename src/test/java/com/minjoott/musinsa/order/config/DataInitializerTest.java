package com.minjoott.musinsa.order.config;

import com.minjoott.musinsa.order.repository.InventoryRepository;
import com.minjoott.musinsa.order.repository.ProductRepository;
import com.minjoott.musinsa.order.repository.WarehouseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class DataInitializerTest {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Test
    @DisplayName("초기 데이터가 정상적으로 로딩된다")
    void initializeDataSuccessfully() {
        assertThat(warehouseRepository.findAll()).hasSize(3);
        assertThat(warehouseRepository.findByName("서울 물류센터")).isPresent();
        assertThat(warehouseRepository.findByName("부산 물류센터")).isPresent();
        assertThat(warehouseRepository.findByName("대구 물류센터")).isPresent();

        assertThat(productRepository.findAll()).hasSize(4);
        assertThat(productRepository.findByName("오버핏 후드")).isPresent();
        assertThat(productRepository.findByName("슬림 청바지")).isPresent();
        assertThat(productRepository.findByName("스니커즈")).isPresent();
        assertThat(productRepository.findByName("크로스백")).isPresent();

        assertThat(inventoryRepository.findAll()).hasSize(6);
    }

    @Test
    @DisplayName("초기 재고 수량이 정확하게 설정된다")
    void verifyInitialInventoryQuantities() {
        Long seoulId = warehouseRepository.findByName("서울 물류센터").get().getId();
        Long busanId = warehouseRepository.findByName("부산 물류센터").get().getId();
        Long daeguId = warehouseRepository.findByName("대구 물류센터").get().getId();

        Long hoodieId = productRepository.findByName("오버핏 후드").get().getId();
        Long jeansId = productRepository.findByName("슬림 청바지").get().getId();
        Long sneakersId = productRepository.findByName("스니커즈").get().getId();
        Long bagId = productRepository.findByName("크로스백").get().getId();

        assertThat(inventoryRepository.findByWarehouseIdAndProductId(seoulId, hoodieId).get().getQuantity())
                .isEqualTo(50);
        assertThat(inventoryRepository.findByWarehouseIdAndProductId(seoulId, jeansId).get().getQuantity())
                .isEqualTo(30);
        assertThat(inventoryRepository.findByWarehouseIdAndProductId(busanId, hoodieId).get().getQuantity())
                .isEqualTo(40);
        assertThat(inventoryRepository.findByWarehouseIdAndProductId(busanId, sneakersId).get().getQuantity())
                .isEqualTo(20);
        assertThat(inventoryRepository.findByWarehouseIdAndProductId(daeguId, jeansId).get().getQuantity())
                .isEqualTo(25);
        assertThat(inventoryRepository.findByWarehouseIdAndProductId(daeguId, bagId).get().getQuantity())
                .isEqualTo(15);
    }
}
