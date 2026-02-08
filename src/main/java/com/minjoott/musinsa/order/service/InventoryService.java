package com.minjoott.musinsa.order.service;

import com.minjoott.musinsa.order.domain.entity.Inventory;
import com.minjoott.musinsa.order.domain.entity.Product;
import com.minjoott.musinsa.order.domain.dto.InventoryResponse;
import com.minjoott.musinsa.order.domain.dto.WarehouseInventoryDto;
import com.minjoott.musinsa.order.exception.InventoryNotFoundException;
import com.minjoott.musinsa.order.exception.ProductNotFoundException;
import com.minjoott.musinsa.order.repository.InventoryRepository;
import com.minjoott.musinsa.order.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryResponse getInventoryByProductId(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        List<Inventory> inventories = inventoryRepository.findByProductIdWithWarehouseAndProduct(productId);

        if (inventories.isEmpty()) {
            throw new InventoryNotFoundException(productId);
        }

        int totalQuantity = inventories.stream()
                .mapToInt(Inventory::getQuantity)
                .sum();

        List<WarehouseInventoryDto> warehouseInventories = inventories.stream()
                .map(inventory -> new WarehouseInventoryDto(
                        inventory.getWarehouse().getId(),
                        inventory.getWarehouse().getName(),
                        inventory.getQuantity()
                ))
                .toList();

        log.debug("상품 재고 조회 완료: productId={}, totalQuantity={}", productId, totalQuantity);

        return new InventoryResponse(
                product.getId(),
                product.getName(),
                totalQuantity,
                warehouseInventories
        );
    }
}
