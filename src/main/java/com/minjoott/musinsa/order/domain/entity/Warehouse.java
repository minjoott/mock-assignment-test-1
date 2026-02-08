package com.minjoott.musinsa.order.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "warehouses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false)
    private int shippingCost;

    @Column(nullable = false)
    private int shippingDays;

    public Warehouse(String name, int shippingCost, int shippingDays) {
        this.name = name;
        this.shippingCost = shippingCost;
        this.shippingDays = shippingDays;
    }
}
