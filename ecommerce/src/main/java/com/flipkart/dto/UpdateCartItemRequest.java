package com.flipkart.dto;
import jakarta.validation.constraints.*;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor
public class UpdateCartItemRequest {
    @Min(0) @NotNull private Integer quantity;
}
