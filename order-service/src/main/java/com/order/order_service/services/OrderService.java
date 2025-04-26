package com.order.order_service.services;

import com.order.order_service.dto.requests.OrderCreateRequest;
import com.order.order_service.dto.requests.OrderItemCreateRequest;
import com.order.order_service.dto.responses.OrderCreateResponse;
import com.order.order_service.dto.responses.OrderItemCreateResponse;
import com.order.order_service.dto.responses.OrderLocationResponse;
import com.order.order_service.models.*;
import com.order.order_service.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Controller
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderCreateResponse createOrder(OrderCreateRequest request, Long userId) {
        validateUserId(userId);
        validateRestaurantId(request.getRestaurantId());
        for (OrderItemCreateRequest item : request.getItems()) {
            validateProductId(item.getProductId());
        }
        validatePaymentMethod(request.getPaymentMethod());

        OrderLocation location = new OrderLocation();
        location.setAddress(request.getLocation().getAddress());
        location.setLatitude(request.getLocation().getLatitude());
        location.setLongitude(request.getLocation().getLongitude());

        Order order = new Order();
        order.setUserId(userId);
        order.setRestaurantId(request.getRestaurantId());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setDiscount(request.getDiscount());
        order.setDeliveryFee(request.getDeliveryFee());
        order.setOrderTotal();
        order.setFinalPrice();
        order.setSpecialNote(request.getSpecialNote());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setEmail(request.getEmail());
        order.setDeliveryUserName(request.getDeliveryUserName());
        order.setDeliveryUserPhoneNumber(request.getDeliveryUserPhoneNumber());
        order.setEstimatedDeliveryTime(request.getEstimatedDeliveryTime());
        order.setLocation(location);
        order.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()));

        List<OrderItem> items = new ArrayList<>();
        for (OrderItemCreateRequest item : request.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(item.getProductId());
            orderItem.setProductName(item.getProductName());
            orderItem.setProductPrice(item.getProductPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setDiscount(item.getDiscount());
            orderItem.setTotalPrice();
            orderItem.setOrder(order);

            items.add(orderItem);
        }

        order.setItems(items);
        orderRepository.save(order);

        return mapToOrderCreateResponse(order);
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new RuntimeException("user id must not be null");
        }
    }

    private void validateRestaurantId(Long restaurantId) {
        if (restaurantId == null) {
            throw new RuntimeException("restaurant id must not be null");
        }
    }

    private void validateProductId(Long productId) {
        if (productId == null) {
            throw new RuntimeException("product id must not be null");
        }
    }

    private void validatePaymentMethod(String paymentMethod) {
        if (paymentMethod == null) {
            throw new RuntimeException("payment method must not be null");
        }
        for (PaymentMethod pm : PaymentMethod.values()) {
            if (pm.name().equalsIgnoreCase(paymentMethod)) {
                return;
            }
        }
        throw new RuntimeException("payment method not valid");
    }

    private OrderCreateResponse mapToOrderCreateResponse(Order order) {
        return OrderCreateResponse.builder()
                .id(order.getId())
                .restaurantId(order.getRestaurantId())
                .userId(order.getUserId())
                .paymentMethod(order.getPaymentMethod().name())
                .orderTotal(order.getOrderTotal())
                .discount(order.getDiscount())
                .deliveryFee(order.getDeliveryFee())
                .finalPrice(order.getFinalPrice())
                .orderStatus(order.getOrderStatus().name())
                .paymentStatus(order.getPaymentStatus().name())
                .deliveryAddress(order.getDeliveryAddress())
                .phoneNumber(order.getPhoneNumber())
                .email(order.getEmail())
                .deliveryUserName(order.getDeliveryUserName())
                .deliveryUserPhoneNumber(order.getDeliveryUserPhoneNumber())
                .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
                .specialNote(order.getSpecialNote())
                .location(mapToOrderLocationResponse(order.getLocation()))
                .items(order.getItems().stream()
                        .map(this::mapToOrderItemCreateRequest)
                        .toList())
                .build();
    }

    private OrderItemCreateResponse mapToOrderItemCreateRequest(OrderItem item) {
        return OrderItemCreateResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .productPrice(item.getProductPrice())
                .quantity(item.getQuantity())
                .discount(item.getDiscount())
                .notes(item.getNotes())
                .total(item.getTotalPrice())
                .build();
    }

    private OrderLocationResponse mapToOrderLocationResponse(OrderLocation location) {
        return OrderLocationResponse.builder()
                .id(location.getId())
                .address(location.getAddress())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }
}
