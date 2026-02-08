package com.minjoott.musinsa.order.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shipments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(nullable = false)
    private int shippingCost;

    @Column(nullable = false)
    private int shippingDays;

    public Shipment(Order order, Warehouse warehouse, int shippingCost, int shippingDays) {
        this.order = order;
        this.warehouse = warehouse;
        this.shippingCost = shippingCost;
        this.shippingDays = shippingDays;
    }
}
