package com.flipkart.dto;
import com.flipkart.model.Order;
import jakarta.validation.constraints.*;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PlaceOrderRequest {
    @NotNull private Long addressId;
    @NotNull private Order.PaymentMethod paymentMethod;
    private String couponCode;
    private String paymentTransactionId;
}
