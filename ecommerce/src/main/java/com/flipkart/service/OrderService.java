package com.flipkart.service;

import com.flipkart.dto.*;
import com.flipkart.exception.*;
import com.flipkart.model.*;
import com.flipkart.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final CouponRepository couponRepository;
    private final UserService userService;

    @Transactional
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        User user = userService.getCurrentUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot place order with empty cart");
        }

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", request.getAddressId()));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Address does not belong to user");
        }

        // Validate stock and build order items
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BadRequestException("Insufficient stock for: " + product.getName());
            }

            BigDecimal itemTotal = cartItem.getPriceAtAddition().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(itemTotal);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .productImage(product.getImages().isEmpty() ? null : product.getImages().get(0))
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPriceAtAddition())
                    .totalPrice(itemTotal)
                    .build();
            orderItems.add(orderItem);

            // Deduct stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            product.setSoldCount(product.getSoldCount() + cartItem.getQuantity());
            productRepository.save(product);
        }

        // Apply coupon
        BigDecimal discount = BigDecimal.ZERO;
        String couponCode = null;
        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            Coupon coupon = couponRepository.findByCode(request.getCouponCode())
                    .orElseThrow(() -> new BadRequestException("Invalid coupon code"));
            if (!coupon.isValid()) throw new BadRequestException("Coupon is expired or invalid");
            discount = coupon.calculateDiscount(subtotal);
            couponCode = coupon.getCode();
            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);
        }

        BigDecimal shippingCharge = subtotal.compareTo(BigDecimal.valueOf(499)) >= 0
                ? BigDecimal.ZERO : BigDecimal.valueOf(49);

        BigDecimal total = subtotal.subtract(discount).add(shippingCharge);

        Order order = Order.builder()
                .user(user)
                .status(Order.OrderStatus.CONFIRMED)
                .subtotal(subtotal)
                .shippingCharge(shippingCharge)
                .discount(discount)
                .totalAmount(total)
                .shippingFullName(address.getFullName())
                .shippingPhone(address.getPhone())
                .shippingAddressLine1(address.getAddressLine1())
                .shippingAddressLine2(address.getAddressLine2())
                .shippingCity(address.getCity())
                .shippingState(address.getState())
                .shippingPincode(address.getPincode())
                .shippingCountry(address.getCountry())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(request.getPaymentMethod() == Order.PaymentMethod.CASH_ON_DELIVERY
                        ? Order.PaymentStatus.PENDING : Order.PaymentStatus.PAID)
                .paymentTransactionId(request.getPaymentTransactionId())
                .couponCode(couponCode)
                .build();

        order = orderRepository.save(order);

        // Set order reference on items
        final Order savedOrder = order;
        orderItems.forEach(item -> item.setOrder(savedOrder));
        savedOrder.setItems(orderItems);
        orderRepository.save(savedOrder);

        // Clear cart
        cartItemRepository.deleteByCartId(cart.getId());

        return mapToResponse(savedOrder);
    }

    public PageResponse<OrderResponse> getUserOrders(int page, int size) {
        User user = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        return PageResponse.<OrderResponse>builder()
                .content(orders.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()))
                .page(orders.getNumber())
                .size(orders.getSize())
                .totalElements(orders.getTotalElements())
                .totalPages(orders.getTotalPages())
                .first(orders.isFirst())
                .last(orders.isLast())
                .build();
    }

    public OrderResponse getOrderById(Long id) {
        User user = userService.getCurrentUser();
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        if (!order.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new UnauthorizedException("Order does not belong to you");
        }
        return mapToResponse(order);
    }

    public OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderNumber));
        return mapToResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long id, String reason) {
        User user = userService.getCurrentUser();
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Order does not belong to you");
        }

        if (order.getStatus() == Order.OrderStatus.DELIVERED ||
            order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot cancel order in status: " + order.getStatus());
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setCancellationReason(reason);

        // Restore stock
        for (OrderItem item : order.getItems()) {
            if (item.getProduct() != null) {
                Product p = item.getProduct();
                p.setStockQuantity(p.getStockQuantity() + item.getQuantity());
                p.setSoldCount(Math.max(0, p.getSoldCount() - item.getQuantity()));
                productRepository.save(p);
            }
        }

        return mapToResponse(orderRepository.save(order));
    }

    // Admin methods
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll(Sort.by("createdAt").descending())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        order.setStatus(Order.OrderStatus.valueOf(status.toUpperCase()));
        if (order.getStatus() == Order.OrderStatus.DELIVERED) {
            order.setDeliveredAt(LocalDateTime.now());
            order.setPaymentStatus(Order.PaymentStatus.PAID);
        }
        return mapToResponse(orderRepository.save(order));
    }

    public OrderResponse mapToResponse(Order o) {
        List<OrderItemResponse> items = o.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct() != null ? item.getProduct().getId() : null)
                        .productName(item.getProductName())
                        .productImage(item.getProductImage())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .totalPrice(item.getTotalPrice())
                        .status(item.getStatus().name())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(o.getId())
                .orderNumber(o.getOrderNumber())
                .items(items)
                .status(o.getStatus().name())
                .subtotal(o.getSubtotal())
                .shippingCharge(o.getShippingCharge())
                .discount(o.getDiscount())
                .totalAmount(o.getTotalAmount())
                .shippingFullName(o.getShippingFullName())
                .shippingAddressLine1(o.getShippingAddressLine1())
                .shippingAddressLine2(o.getShippingAddressLine2())
                .shippingCity(o.getShippingCity())
                .shippingState(o.getShippingState())
                .shippingPincode(o.getShippingPincode())
                .shippingPhone(o.getShippingPhone())
                .paymentMethod(o.getPaymentMethod() != null ? o.getPaymentMethod().name() : null)
                .paymentStatus(o.getPaymentStatus() != null ? o.getPaymentStatus().name() : null)
                .couponCode(o.getCouponCode())
                .createdAt(o.getCreatedAt())
                .deliveredAt(o.getDeliveredAt())
                .build();
    }
}
