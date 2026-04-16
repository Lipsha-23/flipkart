package com.flipkart.dto;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
@Data @NoArgsConstructor @AllArgsConstructor
public class ApplyCouponRequest {
    @NotBlank private String code;
    @NotNull private BigDecimal orderAmount;
}
