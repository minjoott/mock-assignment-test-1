package com.minjoott.musinsa.order.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class InventoryResponse {
    private Long productId;
    private String productName;
    private int totalQuantity;
    private List<WarehouseInventoryDto> inventories;
}
