package com.order.order_service.dto.responses;

import com.order.order_service.dto.requests.OrderItemCreateRequest;
import com.order.order_service.dto.requests.OrderLocationCreateRequest;
import com.order.order_service.models.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderCreateResponse {
    private Long id;
    private Long restaurantId;
    private Long userId;
    private String paymentMethod;
    private Double orderTotal;
    private Double discount;
    private Double deliveryFee;
    private Double finalPrice;
    private String orderStatus;
    private String paymentStatus;
    private String deliveryAddress;
    private String phoneNumber;
    private String email;
    private String deliveryUserName;
    private String deliveryUserPhoneNumber;
    private LocalDateTime estimatedDeliveryTime;
    private String specialNote;
    private OrderLocationResponse location;
    private List<OrderItemCreateResponse> items;
    private OrderStatus status;
}
