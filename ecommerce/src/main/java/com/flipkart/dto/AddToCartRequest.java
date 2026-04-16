package com.flipkart.dto;
import jakarta.validation.constraints.*;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor
public class AddToCartRequest {
    @NotNull private Long productId;
    @Min(1) private Integer quantity = 1;
}
