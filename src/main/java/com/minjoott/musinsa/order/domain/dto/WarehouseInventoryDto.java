package com.minjoott.musinsa.order.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WarehouseInventoryDto {
    private Long warehouseId;
    private String warehouseName;
    private int quantity;
}
