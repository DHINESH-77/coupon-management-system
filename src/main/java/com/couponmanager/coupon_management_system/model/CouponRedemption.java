package com.couponmanager.coupon_management_system.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupon_redemptions")
@Data
public class CouponRedemption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long redemptionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "redemption_date")
    private LocalDateTime redemptionDate;

    @Column(name = "discount_applied", nullable = false)
    private BigDecimal discountApplied;

    @PrePersist
    protected void onCreate() {
        redemptionDate = LocalDateTime.now();
    }
}