package com.couponmanager.coupon_management_system.service;

import com.couponmanager.coupon_management_system.dto.RedemptionRequestDTO;
import com.couponmanager.coupon_management_system.dto.RedemptionResponseDTO;
import com.couponmanager.coupon_management_system.model.*;
import com.couponmanager.coupon_management_system.repository.CouponRedemptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class CouponRedemptionService {

    @Autowired
    private CouponRedemptionRepository couponRedemptionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private ProductService productService;

    @Transactional
    public RedemptionResponseDTO applyCoupon(RedemptionRequestDTO request) {
        RedemptionResponseDTO response = new RedemptionResponseDTO();

        // 1. Validate Entities Exist
        Optional<User> userOpt = userService.getUserById(request.getUserId());
        Optional<Product> productOpt = productService.getProductById(request.getProductId());
        Optional<Coupon> couponOpt = couponService.getCouponByCode(request.getCouponCode());

        if (userOpt.isEmpty() || productOpt.isEmpty() || couponOpt.isEmpty()) {
            response.setSuccess(false);
            response.setMessage(userOpt.isEmpty() ? "User not found"
                    : productOpt.isEmpty() ? "Product not found" : "Coupon not found");
            return response;
        }

        Coupon coupon = couponOpt.get();
        Product product = productOpt.get();
        User user = userOpt.get();

        // 2. Check Date Validity
        if (!couponService.isValidCoupon(coupon.getCode())) {
            response.setSuccess(false);
            response.setMessage("Coupon is expired or not yet active");
            return response;
        }

        // 3. Check Usage Limit (FIXED)
        long currentUsage = couponRedemptionRepository.countByCouponCouponId(coupon.getCouponId());
        if (coupon.getUsageLimit() != null && currentUsage >= coupon.getUsageLimit()) {
            response.setSuccess(false);
            response.setMessage("Coupon usage limit has been reached");
            return response;
        }

        // 4. Calculate Discount (FIXED BigDecimal Division)
        BigDecimal discountAmount = calculateDiscount(product.getPrice(), coupon);
        BigDecimal finalPrice = product.getPrice().subtract(discountAmount);

        // 5. Create and save redemption
        CouponRedemption redemption = new CouponRedemption();
        redemption.setUser(user);
        redemption.setCoupon(coupon);
        redemption.setProduct(product);
        redemption.setDiscountApplied(discountAmount);

        couponRedemptionRepository.save(redemption);

        response.setSuccess(true);
        response.setMessage("Coupon applied successfully");
        response.setOriginalPrice(product.getPrice());
        response.setDiscountAmount(discountAmount);
        response.setFinalPrice(finalPrice);

        return response;
    }

    private BigDecimal calculateDiscount(BigDecimal price, Coupon coupon) {
        BigDecimal discount;
        if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
            BigDecimal percentage = coupon.getDiscountValue().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            discount = price.multiply(percentage).setScale(2, RoundingMode.HALF_UP);
        } else if ("FIXED_AMOUNT".equalsIgnoreCase(coupon.getDiscountType())) {
            discount = coupon.getDiscountValue();
        } else {
            discount = BigDecimal.ZERO;
        }

        // FIXED: Cap the discount at the product price
        if (discount.compareTo(price) > 0) {
            return price;
        }
        return discount;
    }

    // Optimized calculation methods (moving from slow stream to fast SQL)
    public java.math.BigDecimal calculateTotalSavings() {
        return couponRedemptionRepository.sumDiscountApplied();
    }

    public java.math.BigDecimal calculateTotalRevenue() {
        return couponRedemptionRepository.sumTotalRevenue();
    }

    public List<CouponRedemption> getAllRedemptions() {
        return couponRedemptionRepository.findAll();
    }

    public List<CouponRedemption> getRedemptionsByUser(Long userId) {
        return couponRedemptionRepository.findByUserUserId(userId);
    }
}
