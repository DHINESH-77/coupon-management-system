package com.couponmanager.coupon_management_system.controller;

import com.couponmanager.coupon_management_system.dto.RedemptionRequestDTO;
import com.couponmanager.coupon_management_system.dto.RedemptionResponseDTO;
import com.couponmanager.coupon_management_system.model.CouponRedemption;
import com.couponmanager.coupon_management_system.service.CouponRedemptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/redeem")
public class RedemptionController {

    @Autowired
    private CouponRedemptionService couponRedemptionService;

    @PostMapping
    public ResponseEntity<RedemptionResponseDTO> applyCoupon(@RequestBody RedemptionRequestDTO request) {
        RedemptionResponseDTO response = couponRedemptionService.applyCoupon(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CouponRedemption>> getRedemptionsByUser(@PathVariable Long userId) {
        List<CouponRedemption> redemptions = couponRedemptionService.getRedemptionsByUser(userId);
        return ResponseEntity.ok(redemptions);
    }
}