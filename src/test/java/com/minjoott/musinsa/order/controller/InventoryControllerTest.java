package com.minjoott.musinsa.order.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("상품별 재고 조회 API가 정상 동작한다")
    void getInventoryByProductIdSuccessfully() throws Exception {
        mockMvc.perform(get("/api/inventories/product/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.productName").value("오버핏 후드"))
                .andExpect(jsonPath("$.totalQuantity").value(90))
                .andExpect(jsonPath("$.inventories").isArray())
                .andExpect(jsonPath("$.inventories.length()").value(2));
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회 시 404를 반환한다")
    void returnNotFoundWhenProductNotExists() throws Exception {
        mockMvc.perform(get("/api/inventories/product/999"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("상품을 찾을 수 없습니다. productId: 999"));
    }

    @Test
    @DisplayName("재고가 없는 상품 조회 시 404를 반환한다")
    void returnNotFoundWhenInventoryNotExists() throws Exception {
        mockMvc.perform(get("/api/inventories/product/4"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("재고를 찾을 수 없습니다. productId: 4"));
    }

    @Test
    @DisplayName("창고별 재고 정보가 정확하게 반환된다")
    void returnWarehouseInventoriesCorrectly() throws Exception {
        mockMvc.perform(get("/api/inventories/product/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inventories[0].warehouseId").exists())
                .andExpect(jsonPath("$.inventories[0].warehouseName").exists())
                .andExpect(jsonPath("$.inventories[0].quantity").exists());
    }
}
