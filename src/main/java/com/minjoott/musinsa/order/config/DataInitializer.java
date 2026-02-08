package com.minjoott.musinsa.order.config;

import com.minjoott.musinsa.order.domain.entity.Inventory;
import com.minjoott.musinsa.order.domain.entity.Product;
import com.minjoott.musinsa.order.domain.entity.Warehouse;
import com.minjoott.musinsa.order.repository.InventoryRepository;
import com.minjoott.musinsa.order.repository.ProductRepository;
import com.minjoott.musinsa.order.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        log.info("초기 데이터 로딩 시작");

        Warehouse seoul = createWarehouse("서울 물류센터", 3000, 1);
        Warehouse busan = createWarehouse("부산 물류센터", 5000, 2);
        Warehouse daegu = createWarehouse("대구 물류센터", 4000, 2);

        Product hoodie = createProduct("오버핏 후드", 39000);
        Product jeans = createProduct("슬림 청바지", 59000);
        Product sneakers = createProduct("스니커즈", 89000);
        Product bag = createProduct("크로스백", 45000);

        createInventory(seoul, hoodie, 50);
        createInventory(seoul, jeans, 30);
        createInventory(busan, hoodie, 40);
        createInventory(busan, sneakers, 20);
        createInventory(daegu, jeans, 25);
        createInventory(daegu, bag, 15);

        log.info("초기 데이터 로딩 완료");
    }

    private Warehouse createWarehouse(String name, int shippingCost, int shippingDays) {
        return warehouseRepository.findByName(name)
                .orElseGet(() -> {
                    Warehouse warehouse = new Warehouse(name, shippingCost, shippingDays);
                    warehouseRepository.save(warehouse);
                    log.debug("창고 생성: {}", name);
                    return warehouse;
                });
    }

    private Product createProduct(String name, int price) {
        return productRepository.findByName(name)
                .orElseGet(() -> {
                    Product product = new Product(name, price);
                    productRepository.save(product);
                    log.debug("상품 생성: {}", name);
                    return product;
                });
    }

    private void createInventory(Warehouse warehouse, Product product, int quantity) {
        inventoryRepository.findByWarehouseIdAndProductId(warehouse.getId(), product.getId())
                .orElseGet(() -> {
                    Inventory inventory = new Inventory(warehouse, product, quantity);
                    inventoryRepository.save(inventory);
                    log.debug("재고 생성: {} - {} ({}개)", warehouse.getName(), product.getName(), quantity);
                    return inventory;
                });
    }
}
