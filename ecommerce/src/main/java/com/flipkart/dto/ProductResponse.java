package com.flipkart.dto;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer discountPercentage;
    private Integer stockQuantity;
    private CategoryResponse category;
    private String brand;
    private List<String> images;
    private Double averageRating;
    private Integer totalReviews;
    private boolean active;
    private boolean featured;
    private Map<String, String> specifications;
    private String sku;
    private Integer soldCount;
    private LocalDateTime createdAt;
    private UserResponse seller;
}
