package com.flipkart.controller;

import com.flipkart.dto.*;
import com.flipkart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart management")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get current user's cart")
    public ApiResponse<CartResponse> getCart() {
        return ApiResponse.success(cartService.getCart());
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart")
    public ApiResponse<CartResponse> addItem(@Valid @RequestBody AddToCartRequest request) {
        return ApiResponse.success(cartService.addToCart(request), "Item added to cart");
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item quantity (0 = remove)")
    public ApiResponse<CartResponse> updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ApiResponse.success(cartService.updateCartItem(itemId, request), "Cart updated");
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove item from cart")
    public ApiResponse<CartResponse> removeItem(@PathVariable Long itemId) {
        return ApiResponse.success(cartService.removeFromCart(itemId), "Item removed from cart");
    }
}
