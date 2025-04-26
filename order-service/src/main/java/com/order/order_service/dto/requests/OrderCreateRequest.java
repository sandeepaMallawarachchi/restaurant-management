package com.order.order_service.dto.requests;

import com.order.order_service.models.OrderLocation;
import com.order.order_service.models.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {

    private Long restaurantId;
    private String paymentMethod;
    private Double discount;
    private Double deliveryFee;
    private String orderStatus;
    private String paymentStatus;
    private String deliveryAddress;
    private String phoneNumber;
    private String email;
    private String deliveryUserName;
    private String deliveryUserPhoneNumber;
    private LocalDateTime estimatedDeliveryTime;
    private String specialNote;
    private OrderLocationCreateRequest location;
    private List<OrderItemCreateRequest> items;

}