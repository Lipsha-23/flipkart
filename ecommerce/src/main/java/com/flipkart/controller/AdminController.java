package com.flipkart.controller;

import com.flipkart.dto.*;
import com.flipkart.model.*;
import com.flipkart.repository.*;
import com.flipkart.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin", description = "Admin-only management endpoints")
public class AdminController {

    private final UserService userService;
    private final OrderService orderService;
    private final CategoryService categoryService;
    private final ProductService productService;
    private final CouponService couponService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    // ─── DASHBOARD ────────────────────────────────────────────────────────────

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard statistics")
    public ApiResponse<DashboardStats> getDashboard() {
        BigDecimal revenue = orderRepository.getTotalRevenue();
        DashboardStats stats = DashboardStats.builder()
                .totalUsers(userRepository.count())
                .totalProducts(productRepository.countByActiveTrue())
                .totalOrders(orderRepository.count())
                .pendingOrders(orderRepository.countByStatus(Order.OrderStatus.PENDING)
                        + orderRepository.countByStatus(Order.OrderStatus.CONFIRMED))
                .totalRevenue(revenue != null ? revenue : BigDecimal.ZERO)
                .totalCategories(categoryRepository.count())
                .build();
        return ApiResponse.success(stats);
    }

    // ─── USER MANAGEMENT ─────────────────────────────────────────────────────

    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.success(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID")
    public ApiResponse<UserResponse> getUser(@PathVariable Long id) {
        return ApiResponse.success(userService.getUserById(id));
    }

    @PatchMapping("/users/{id}/toggle")
    @Operation(summary = "Enable/disable user account")
    public ApiResponse<Void> toggleUser(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return ApiResponse.success(null, "User status updated");
    }

    @PostMapping("/users/seller")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a seller account")
    public ApiResponse<UserResponse> createSeller(@RequestBody RegisterRequest request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(User.Role.SELLER)
                .build();
        user = userRepository.save(user);
        return ApiResponse.success(userService.mapToResponse(user), "Seller account created");
    }

    // ─── ORDER MANAGEMENT ────────────────────────────────────────────────────

    @GetMapping("/orders")
    @Operation(summary = "Get all orders")
    public ApiResponse<List<OrderResponse>> getAllOrders() {
        return ApiResponse.success(orderService.getAllOrders());
    }

    @GetMapping("/orders/{id}")
    @Operation(summary = "Get order by ID")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Long id) {
        return ApiResponse.success(orderService.getOrderById(id));
    }

    @PatchMapping("/orders/{id}/status")
    @Operation(summary = "Update order status")
    public ApiResponse<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ApiResponse.success(orderService.updateOrderStatus(id, status), "Order status updated");
    }

    // ─── CATEGORY MANAGEMENT ─────────────────────────────────────────────────

    @GetMapping("/categories")
    @Operation(summary = "Get all categories including inactive")
    public ApiResponse<List<CategoryResponse>> getAllCategories() {
        return ApiResponse.success(categoryService.getAllCategories());
    }

    // ─── COUPON MANAGEMENT ───────────────────────────────────────────────────

    @PostMapping("/coupons")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a coupon")
    public ApiResponse<CouponResponse> createCoupon(@RequestBody Coupon coupon) {
        return ApiResponse.success(couponService.createCoupon(coupon), "Coupon created");
    }

    @GetMapping("/coupons")
    @Operation(summary = "Get all coupons")
    public ApiResponse<List<CouponResponse>> getCoupons() {
        return ApiResponse.success(couponService.getAllCoupons());
    }

    // ─── SEED DATA ───────────────────────────────────────────────────────────

    @PostMapping("/seed")
    @Operation(summary = "Seed demo data (dev only)")
    public ApiResponse<String> seedData() {
        return ApiResponse.success("Data seeding is handled via DataInitializer on startup", "OK");
    }
}
