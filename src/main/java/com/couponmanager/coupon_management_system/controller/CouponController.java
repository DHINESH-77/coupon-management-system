package com.couponmanager.coupon_management_system.controller;

import com.couponmanager.coupon_management_system.model.Coupon;
import com.couponmanager.coupon_management_system.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
        Coupon savedCoupon = couponService.createCoupon(coupon);
        return ResponseEntity.ok(savedCoupon);
    }

    @GetMapping
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        List<Coupon> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable Long id) {
        Optional<Coupon> coupon = couponService.getCouponById(id);
        return coupon.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Coupon> getCouponByCode(@PathVariable String code) {
        Optional<Coupon> coupon = couponService.getCouponByCode(code);
        return coupon.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<Boolean> validateCoupon(@PathVariable String code) {
        boolean isValid = couponService.isValidCoupon(code);
        return ResponseEntity.ok(isValid);
    }
}