package com.flipkart.controller;

import com.flipkart.dto.*;
import com.flipkart.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// ==================== REVIEW CONTROLLER ====================
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Product reviews and ratings")
class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get reviews for a product")
    public ApiResponse<PageResponse<ReviewResponse>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(reviewService.getProductReviews(productId, page, size));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a review (authenticated)")
    public ApiResponse<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request) {
        return ApiResponse.success(reviewService.createReview(request), "Review submitted");
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a review")
    public ApiResponse<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ApiResponse.success(null, "Review deleted");
    }
}

// ==================== ADDRESS CONTROLLER ====================
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Tag(name = "Addresses", description = "Shipping address management")
@SecurityRequirement(name = "bearerAuth")
class AddressController {
    private final AddressService addressService;

    @GetMapping
    @Operation(summary = "Get all addresses of current user")
    public ApiResponse<List<AddressResponse>> getAddresses() {
        return ApiResponse.success(addressService.getUserAddresses());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a new address")
    public ApiResponse<AddressResponse> addAddress(@Valid @RequestBody AddressRequest request) {
        return ApiResponse.success(addressService.addAddress(request), "Address added");
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an address")
    public ApiResponse<AddressResponse> updateAddress(
            @PathVariable Long id, @Valid @RequestBody AddressRequest request) {
        return ApiResponse.success(addressService.updateAddress(id, request), "Address updated");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an address")
    public ApiResponse<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ApiResponse.success(null, "Address deleted");
    }
}

// ==================== WISHLIST CONTROLLER ====================
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "Product wishlist management")
@SecurityRequirement(name = "bearerAuth")
class WishlistController {
    private final WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Get wishlist items")
    public ApiResponse<List<ProductResponse>> getWishlist() {
        return ApiResponse.success(wishlistService.getWishlist());
    }

    @PostMapping("/toggle/{productId}")
    @Operation(summary = "Toggle product in wishlist (add/remove)")
    public ApiResponse<Map<String, String>> toggleWishlist(@PathVariable Long productId) {
        String action = wishlistService.toggleWishlist(productId);
        return ApiResponse.success(Map.of("action", action, "productId", productId.toString()),
                "Product " + action + " wishlist");
    }

    @GetMapping("/check/{productId}")
    @Operation(summary = "Check if product is in wishlist")
    public ApiResponse<Map<String, Boolean>> checkWishlist(@PathVariable Long productId) {
        return ApiResponse.success(Map.of("inWishlist", wishlistService.isInWishlist(productId)));
    }
}

// ==================== COUPON CONTROLLER ====================
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupons", description = "Coupon validation and application")
class CouponController {
    private final CouponService couponService;

    @PostMapping("/validate")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Validate and calculate coupon discount")
    public ApiResponse<CouponResponse> validateCoupon(@Valid @RequestBody ApplyCouponRequest request) {
        return ApiResponse.success(couponService.validateAndApply(request));
    }

    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get all available coupons")
    public ApiResponse<List<CouponResponse>> getAllCoupons() {
        return ApiResponse.success(couponService.getAllCoupons());
    }
}
