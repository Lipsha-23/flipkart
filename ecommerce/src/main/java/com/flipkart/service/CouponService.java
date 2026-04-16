package com.flipkart.service;

import com.flipkart.dto.*;
import com.flipkart.exception.*;
import com.flipkart.model.Coupon;
import com.flipkart.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponResponse validateAndApply(ApplyCouponRequest request) {
        Coupon coupon = couponRepository.findByCode(request.getCode().toUpperCase())
                .orElseThrow(() -> new BadRequestException("Invalid coupon code: " + request.getCode()));

        if (!coupon.isValid()) {
            return CouponResponse.builder().valid(false)
                    .code(coupon.getCode()).description("Coupon is expired or exhausted").build();
        }

        BigDecimal discount = coupon.calculateDiscount(request.getOrderAmount());

        return CouponResponse.builder()
                .id(coupon.getId()).code(coupon.getCode())
                .description(coupon.getDescription())
                .discountType(coupon.getDiscountType().name())
                .discountValue(coupon.getDiscountValue())
                .minOrderAmount(coupon.getMinOrderAmount())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .calculatedDiscount(discount).valid(true).build();
    }

    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream().map(c ->
                CouponResponse.builder().id(c.getId()).code(c.getCode())
                        .description(c.getDescription())
                        .discountType(c.getDiscountType().name())
                        .discountValue(c.getDiscountValue())
                        .minOrderAmount(c.getMinOrderAmount())
                        .maxDiscountAmount(c.getMaxDiscountAmount())
                        .valid(c.isValid()).build())
                .collect(Collectors.toList());
    }

    @Transactional
    public CouponResponse createCoupon(Coupon coupon) {
        if (couponRepository.existsByCode(coupon.getCode())) {
            throw new BadRequestException("Coupon code already exists: " + coupon.getCode());
        }
        coupon = couponRepository.save(coupon);
        return CouponResponse.builder().id(coupon.getId()).code(coupon.getCode())
                .description(coupon.getDescription()).valid(coupon.isValid()).build();
    }
}
