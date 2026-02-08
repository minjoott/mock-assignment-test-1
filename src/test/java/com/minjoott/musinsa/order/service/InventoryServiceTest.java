package com.minjoott.musinsa.order.service;

import com.minjoott.musinsa.order.domain.entity.Inventory;
import com.minjoott.musinsa.order.domain.entity.Product;
import com.minjoott.musinsa.order.domain.entity.Warehouse;
import com.minjoott.musinsa.order.domain.dto.InventoryResponse;
import com.minjoott.musinsa.order.exception.InventoryNotFoundException;
import com.minjoott.musinsa.order.exception.ProductNotFoundException;
import com.minjoott.musinsa.order.repository.InventoryRepository;
import com.minjoott.musinsa.order.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    @DisplayName("상품별 재고를 정상적으로 조회한다")
    void getInventoryByProductIdSuccessfully() {
        Long productId = 1L;
        Product product = new Product("오버핏 후드", 39000);
        Warehouse seoul = new Warehouse("서울 물류센터", 3000, 1);
        Warehouse busan = new Warehouse("부산 물류센터", 5000, 2);

        List<Inventory> inventories = List.of(
                new Inventory(seoul, product, 50),
                new Inventory(busan, product, 40)
        );

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(inventoryRepository.findByProductIdWithWarehouseAndProduct(productId))
                .willReturn(inventories);

        InventoryResponse response = inventoryService.getInventoryByProductId(productId);

        assertThat(response.getProductName()).isEqualTo("오버핏 후드");
        assertThat(response.getTotalQuantity()).isEqualTo(90);
        assertThat(response.getInventories()).hasSize(2);
        assertThat(response.getInventories())
                .extracting("warehouseName")
                .containsExactly("서울 물류센터", "부산 물류센터");
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID로 조회 시 예외가 발생한다")
    void throwsExceptionWhenProductNotFound() {
        Long productId = 999L;

        given(productRepository.findById(productId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.getInventoryByProductId(productId))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("상품을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("재고가 없는 상품 조회 시 예외가 발생한다")
    void throwsExceptionWhenInventoryNotFound() {
        Long productId = 1L;
        Product product = new Product("오버핏 후드", 39000);

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(inventoryRepository.findByProductIdWithWarehouseAndProduct(productId))
                .willReturn(List.of());

        assertThatThrownBy(() -> inventoryService.getInventoryByProductId(productId))
                .isInstanceOf(InventoryNotFoundException.class)
                .hasMessageContaining("재고를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("재고 합산이 정확하게 계산된다")
    void calculateTotalQuantityCorrectly() {
        Long productId = 1L;
        Product product = new Product("오버핏 후드", 39000);
        Warehouse seoul = new Warehouse("서울 물류센터", 3000, 1);
        Warehouse busan = new Warehouse("부산 물류센터", 5000, 2);
        Warehouse daegu = new Warehouse("대구 물류센터", 4000, 2);

        List<Inventory> inventories = List.of(
                new Inventory(seoul, product, 30),
                new Inventory(busan, product, 20),
                new Inventory(daegu, product, 15)
        );

        given(productRepository.findById(productId)).willReturn(Optional.of(product));
        given(inventoryRepository.findByProductIdWithWarehouseAndProduct(productId))
                .willReturn(inventories);

        InventoryResponse response = inventoryService.getInventoryByProductId(productId);

        assertThat(response.getTotalQuantity()).isEqualTo(65);
    }
}
