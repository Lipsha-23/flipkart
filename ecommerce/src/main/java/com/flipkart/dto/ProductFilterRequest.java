package com.flipkart.dto;
import lombok.*;
import java.math.BigDecimal;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductFilterRequest {
    private Long categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String brand;
    private Integer minRating;
    private String sortBy;
    private int page = 0;
    private int size = 20;
}
