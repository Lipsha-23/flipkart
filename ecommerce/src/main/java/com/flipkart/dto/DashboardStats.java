package com.flipkart.dto;
import lombok.*;
import java.math.BigDecimal;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardStats {
    private Long totalUsers;
    private Long totalProducts;
    private Long totalOrders;
    private Long pendingOrders;
    private BigDecimal totalRevenue;
    private Long totalCategories;
}
