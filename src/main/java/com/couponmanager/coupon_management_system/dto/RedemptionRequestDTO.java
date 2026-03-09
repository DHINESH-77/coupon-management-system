package com.couponmanager.coupon_management_system.dto;

import lombok.Data;

@Data
public class RedemptionRequestDTO {
    private Long userId;
    private String couponCode;
    private Long productId;
}