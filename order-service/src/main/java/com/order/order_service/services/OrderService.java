package com.order.order_service.services;

import com.order.order_service.dto.requests.OrderCreateRequest;
import com.order.order_service.dto.requests.OrderItemCreateRequest;
import com.order.order_service.dto.responses.OrderCreateResponse;
import com.order.order_service.dto.responses.OrderItemCreateResponse;
import com.order.order_service.dto.responses.OrderLocationResponse;
import com.order.order_service.models.*;
import com.order.order_service.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;

    public OrderCreateResponse createOrder(OrderCreateRequest request, Long userId) {
        logger.info("Creating new order for user: {}, restaurant: {}", userId, request.getRestaurantId());

        try {
            validateUserId(userId);
            validateRestaurantId(request.getRestaurantId());

            logger.debug("Validating {} order items", request.getItems().size());
            for (OrderItemCreateRequest item : request.getItems()) {
                validateProductId(item.getProductId());
            }

            validatePaymentMethod(request.getPaymentMethod());
            logger.debug("All validations passed successfully");

            OrderLocation location = new OrderLocation();
            location.setAddress(request.getLocation().getAddress());
            location.setLatitude(request.getLocation().getLatitude());
            location.setLongitude(request.getLocation().getLongitude());

            Order order = new Order();
            order.setUserId(userId);
            order.setRestaurantId(request.getRestaurantId());
            order.setOrderStatus(OrderStatus.PENDING);
            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setSpecialNote(request.getSpecialNote());
            order.setDeliveryAddress(request.getDeliveryAddress());
            order.setPhoneNumber(request.getPhoneNumber());
            order.setEmail(request.getEmail());
            order.setDeliveryUserName(request.getDeliveryUserName());
            order.setDeliveryUserPhoneNumber(request.getDeliveryUserPhoneNumber());
            order.setEstimatedDeliveryTime(request.getEstimatedDeliveryTime());
            order.setLocation(location);
            order.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()));

            logger.debug("Creating order items");
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
                orderItem.setNotes(item.getNotes());

                items.add(orderItem);
            }

            order.setItems(items);
            order.setDiscount(request.getDiscount());
            order.setDeliveryFee(request.getDeliveryFee());
            order.setOrderTotal();
            order.setFinalPrice();
            System.out.println("Order total price: ...................." + order.getOrderTotal());
            System.out.println("Order final price: ...................." + order.getFinalPrice());
            Order savedOrder = orderRepository.save(order);
            logger.info("Order created successfully with ID: {}", savedOrder.getId());

            return mapToOrderCreateResponse(savedOrder);
        } catch (Exception e) {
            logger.error("Error creating order for user: {}, error: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    public OrderCreateResponse getOrder(Long orderId, Long userId) {
        logger.info("Getting order with ID: {} for user: {}", orderId, userId);
        validateUserId(userId);
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException("Order not found")
        );
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new RuntimeException("User ID does not match with the order's user ID");
        }
        return mapToOrderCreateResponse(order);
    }

    public Page<OrderCreateResponse> getAllOrderOfUser(Long userId, int page, int size){
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Order> orders = orderRepository.findAllByUserId(userId, pageable);
        return orders.map(this::mapToOrderCreateResponse);
    }

    private void validateUserId(Long userId) {
        logger.debug("Validating user ID: {}", userId);
        if (userId == null) {
            logger.warn("User ID validation failed: null value");
            throw new RuntimeException("user id must not be null");
        }
    }

    private void validateRestaurantId(Long restaurantId) {
        logger.debug("Validating restaurant ID: {}", restaurantId);
        if (restaurantId == null) {
            logger.warn("Restaurant ID validation failed: null value");
            throw new RuntimeException("restaurant id must not be null");
        }
    }

    private void validateProductId(Long productId) {
        logger.debug("Validating product ID: {}", productId);
        if (productId == null) {
            logger.warn("Product ID validation failed: null value");
            throw new RuntimeException("product id must not be null");
        }
    }

    private void validatePaymentMethod(String paymentMethod) {
        logger.debug("Validating payment method: {}", paymentMethod);
        if (paymentMethod == null) {
            logger.warn("Payment method validation failed: null value");
            throw new RuntimeException("payment method must not be null");
        }
        for (PaymentMethod pm : PaymentMethod.values()) {
            if (pm.name().equalsIgnoreCase(paymentMethod)) {
                return;
            }
        }
        logger.warn("Payment method validation failed: invalid value: {}", paymentMethod);
        throw new RuntimeException("payment method not valid");
    }

    private OrderCreateResponse mapToOrderCreateResponse(Order order) {
        logger.debug("Mapping order to response, order ID: {}", order.getId());
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
        logger.trace("Mapping order item to response, item ID: {}", item.getId());
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
        logger.trace("Mapping order location to response, location ID: {}", location.getId());
        return OrderLocationResponse.builder()
                .id(location.getId())
                .address(location.getAddress())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }

    public Page<OrderCreateResponse> getAll(Integer page, Integer size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(this::mapToOrderCreateResponse);
    }
}