package com.couponmanager.coupon_management_system.repository;

import com.couponmanager.coupon_management_system.model.CouponRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, Long> {
    List<CouponRedemption> findByUserUserId(Long userId);

    List<CouponRedemption> findByUserUserIdOrderByRedemptionDateDesc(Long userId);

    List<CouponRedemption> findByProductProductId(Long productId);

    List<CouponRedemption> findByCouponCouponId(Long couponId);

    long countByCouponCouponId(Long couponId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(cr.discountApplied), 0) FROM CouponRedemption cr")
    java.math.BigDecimal sumDiscountApplied();

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(cr.product.price - cr.discountApplied), 0) FROM CouponRedemption cr")
    java.math.BigDecimal sumTotalRevenue();
}