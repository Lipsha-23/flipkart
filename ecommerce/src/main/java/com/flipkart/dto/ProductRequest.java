package com.flipkart.dto;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductRequest {
    @NotBlank private String name;
    private String description;
    @NotNull @DecimalMin("0.0") private BigDecimal price;
    private BigDecimal originalPrice;
    @Min(0) private Integer stockQuantity;
    @NotNull private Long categoryId;
    private String brand;
    private List<String> images;
    private Map<String, String> specifications;
    private String sku;
    private boolean featured;
}
