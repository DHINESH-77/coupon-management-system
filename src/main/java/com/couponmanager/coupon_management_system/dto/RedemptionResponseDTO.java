package com.couponmanager.coupon_management_system.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RedemptionResponseDTO {
    private boolean success;
    private String message;
    private BigDecimal originalPrice;
    private BigDecimal discountAmount;
    private BigDecimal finalPrice;
}