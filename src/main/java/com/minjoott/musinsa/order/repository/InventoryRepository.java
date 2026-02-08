package com.minjoott.musinsa.order.repository;

import com.minjoott.musinsa.order.domain.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    @Query("SELECT i FROM Inventory i JOIN FETCH i.warehouse JOIN FETCH i.product WHERE i.product.id = :productId")
    List<Inventory> findByProductIdWithWarehouseAndProduct(@Param("productId") Long productId);
    
    @Query("SELECT i FROM Inventory i WHERE i.warehouse.id = :warehouseId AND i.product.id = :productId")
    Optional<Inventory> findByWarehouseIdAndProductId(@Param("warehouseId") Long warehouseId, 
                                                        @Param("productId") Long productId);
}
