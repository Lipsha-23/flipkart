package com.flipkart.dto;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private List<OrderItemResponse> items;
    private String status;
    private BigDecimal subtotal;
    private BigDecimal shippingCharge;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private String shippingFullName;
    private String shippingAddressLine1;
    private String shippingAddressLine2;
    private String shippingCity;
    private String shippingState;
    private String shippingPincode;
    private String shippingPhone;
    private String paymentMethod;
    private String paymentStatus;
    private String couponCode;
    private LocalDateTime createdAt;
    private LocalDateTime deliveredAt;
}
