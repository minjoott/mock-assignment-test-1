package com.minjoott.musinsa.order.repository;

import com.minjoott.musinsa.order.domain.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    
    @Query("SELECT s FROM Shipment s JOIN FETCH s.warehouse WHERE s.order.id = :orderId")
    List<Shipment> findByOrderIdWithWarehouse(@Param("orderId") Long orderId);
}
