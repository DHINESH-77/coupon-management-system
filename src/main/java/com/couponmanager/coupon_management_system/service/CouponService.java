package com.couponmanager.coupon_management_system.service;

import com.couponmanager.coupon_management_system.model.Coupon;
import com.couponmanager.coupon_management_system.model.CouponRedemption;
import com.couponmanager.coupon_management_system.repository.CouponRepository;
import com.couponmanager.coupon_management_system.repository.CouponRedemptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CouponService {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponRedemptionRepository couponRedemptionRepository;

    public Coupon createCoupon(Coupon coupon) {
        // Check if coupon code already exists
        Optional<Coupon> existingCoupon = couponRepository.findByCode(coupon.getCode());
        if (existingCoupon.isPresent()) {
            throw new RuntimeException("Coupon code already exists: " + coupon.getCode());
        }
        return couponRepository.save(coupon);
    }

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    public Optional<Coupon> getCouponById(Long id) {
        return couponRepository.findById(id);
    }

    public Optional<Coupon> getCouponByCode(String code) {
        return couponRepository.findByCode(code);
    }

    public boolean isValidCoupon(String couponCode) {
        Optional<Coupon> couponOpt = couponRepository.findByCode(couponCode);
        if (couponOpt.isEmpty()) {
            return false;
        }

        Coupon coupon = couponOpt.get();
        LocalDate today = LocalDate.now();

        // Check date validity
        boolean dateValid = !today.isBefore(coupon.getStartDate()) && !today.isAfter(coupon.getEndDate());
        if (!dateValid)
            return false;

        // Check usage limit (FIXED: Added usage limit check to validation)
        long currentUsage = couponRedemptionRepository.countByCouponCouponId(coupon.getCouponId());
        if (coupon.getUsageLimit() != null && currentUsage >= coupon.getUsageLimit()) {
            return false;
        }

        return true;
    }

    public void deleteCoupon(Coupon coupon) {
        // First delete all redemption records for this coupon
        List<CouponRedemption> couponRedemptions = couponRedemptionRepository
                .findByCouponCouponId(coupon.getCouponId());
        if (!couponRedemptions.isEmpty()) {
            couponRedemptionRepository.deleteAll(couponRedemptions);
            System.out.println("✅ Deleted " + couponRedemptions.size() + " redemption records for coupon ID: "
                    + coupon.getCouponId());
        }

        // Then delete the coupon
        couponRepository.delete(coupon);
        System.out.println("✅ Deleted coupon with ID: " + coupon.getCouponId());
    }

    public void deleteCouponById(Long id) {
        Optional<Coupon> coupon = couponRepository.findById(id);
        if (coupon.isPresent()) {
            deleteCoupon(coupon.get());
        } else {
            throw new RuntimeException("Coupon not found with ID: " + id);
        }
    }
}