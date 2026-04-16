package com.flipkart.controller;

import com.flipkart.dto.*;
import com.flipkart.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order placement and tracking")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Place a new order from cart")
    public ApiResponse<OrderResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest request) {
        return ApiResponse.success(orderService.placeOrder(request), "Order placed successfully");
    }

    @GetMapping
    @Operation(summary = "Get all orders for current user")
    public ApiResponse<PageResponse<OrderResponse>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(orderService.getUserOrders(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Long id) {
        return ApiResponse.success(orderService.getOrderById(id));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get order by order number")
    public ApiResponse<OrderResponse> getByOrderNumber(@PathVariable String orderNumber) {
        return ApiResponse.success(orderService.getOrderByNumber(orderNumber));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order")
    public ApiResponse<OrderResponse> cancelOrder(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "Cancelled by user") String reason) {
        return ApiResponse.success(orderService.cancelOrder(id, reason), "Order cancelled");
    }
}
