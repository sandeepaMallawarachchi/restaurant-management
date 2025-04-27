package com.order.order_service.services;

import com.order.order_service.dto.requests.OrderCreateRequest;
import com.order.order_service.dto.requests.OrderFilterRequest;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public Page<OrderCreateResponse> getAllOrder(OrderFilterRequest request){
        Pageable pageable = Pageable.ofSize(request.getSize()).withPage(request.getPage());
        Page<Order> orders = orderRepository.filterAll(
                request.getUserId(),
                request.getOrderStatus(),
                request.getRestaurantId(),
                request.getOrderDateStart(),
                request.getOrderDateEnd(),
                request.getPaymentMethod(),
                request.getPaymentStatus(),
                pageable
        );
        return orders.map(this::mapToOrderCreateResponse);
    }

    public OrderCreateResponse changeOrderStatus(Long orderId, OrderStatus orderStatus, Long userId) {
        logger.info("Changing order status for order with ID: {} for user: {}", orderId, userId);
        validateUserId(userId);
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException("Order not found")
        );
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new RuntimeException("User ID does not match with the order's user ID");
        }
        order.setOrderStatus(orderStatus);

        if (orderStatus == OrderStatus.CANCELLED) {
            cancelOrder(order);
        }
        orderRepository.save(order);
        logger.info("Order status changed successfully for order with ID: {}", orderId);

        return mapToOrderCreateResponse(order);
    }

    private void cancelOrder(Order order) {
        logger.info("Canceling order with ID: {}", order.getId());
        order.setOrderStatus(OrderStatus.CANCELLED);

        if(order.getPaymentMethod() == PaymentMethod.CREDIT_CARD_ONLINE){
            if(order.getPaymentStatus() == PaymentStatus.COMPLETED){
                order.setPaymentStatus(PaymentStatus.REFUND_REQUESTED);
            }
            if(order.getPaymentStatus() == PaymentStatus.PENDING){
                order.setPaymentStatus(PaymentStatus.CANCELLED);
            }
        }

        if((order.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY) || (order.getPaymentMethod() == PaymentMethod.CREDIT_CARD_ON_DELIVERY)){
            order.setPaymentStatus(PaymentStatus.CANCELLED);
        }
        logger.info("Order cancelled successfully for order with ID: {}", order.getId());
    }

    public OrderCreateResponse upgradeOrderStatus(Long orderId, Long userId) {
        logger.info("Upgrading order status for order with ID: {} for user: {}", orderId, userId);
        validateUserId(userId);
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException("Order not found")
        );
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new RuntimeException("User ID does not match with the order's user ID");
        }
        if (order.getOrderStatus() == OrderStatus.PENDING) {
            order.setOrderStatus(OrderStatus.CONFIRMED);
        }
        if (order.getOrderStatus() == OrderStatus.CONFIRMED) {
            order.setOrderStatus(OrderStatus.PREPARING);
        }
        if (order.getOrderStatus() == OrderStatus.PREPARING) {
            order.setOrderStatus(OrderStatus.READY_FOR_DELIVERY);
        }
        if (order.getOrderStatus() == OrderStatus.READY_FOR_DELIVERY) {
            order.setOrderStatus(OrderStatus.OUT_FOR_DELIVERY);
        }
        if (order.getOrderStatus() == OrderStatus.OUT_FOR_DELIVERY) {
            order.setOrderStatus(OrderStatus.NEAR_BY);
        }
        if (order.getOrderStatus() == OrderStatus.NEAR_BY) {
            order.setOrderStatus(OrderStatus.DELIVERED);
        }
        orderRepository.save(order);
        logger.info("Order status upgraded successfully for order with ID: {}", orderId);
        return mapToOrderCreateResponse(order);
    }

    public Double getRestaurantTotalIncome(
            Long restaurantId,
            LocalDateTime startDate,
            LocalDateTime endDate
    ){
        if(startDate == null){
            startDate = LocalDate.now().atStartOfDay();
            if(endDate == null){
                endDate = LocalDate.now().atTime(23, 59, 59);
            }
        }
        if(endDate == null){
            endDate = startDate.toLocalDate().atTime(23, 59, 59);
        }
        if(startDate.isAfter(endDate)){
            throw new RuntimeException("Start date must be before end date");
        }

        List<Order> orders = orderRepository.getByRestaurantIdAndDateBetween(restaurantId, startDate, endDate);
        Double totalIncome = 0.0;
        for(Order order : orders){
            totalIncome += order.getFinalPrice();
        }
        return totalIncome;
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

    public OrderCreateResponse updateOrder(
            Long orderId,
            String phoneNumber,
            String email,
            String deliveryUserPhoneNumber,
            String deliveryUserName,
            String specialNote,
            Long userId
    ) {
        logger.info("Updating order with ID: {} for user: {}", orderId, userId);
        validateUserId(userId);
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException("Order not found")
        );
        if (!Objects.equals(order.getUserId(), userId)) {
            throw new RuntimeException("User ID does not match with the order's user ID");
        }

        if((order.getOrderStatus() == OrderStatus.CANCELLED) || (order.getOrderStatus() == OrderStatus.DELIVERED)){
            throw new RuntimeException("Order cannot be updated after it has been cancelled or delivered");
        }
        if (phoneNumber != null) {
            order.setPhoneNumber(phoneNumber);
        }
        if (email != null) {
            order.setEmail(email);
        }
        if (deliveryUserPhoneNumber != null) {
            order.setDeliveryUserPhoneNumber(deliveryUserPhoneNumber);
        }
        if (deliveryUserName != null) {
            order.setDeliveryUserName(deliveryUserName);
        }
        if (specialNote != null) {
            order.setSpecialNote(specialNote);
        }
        orderRepository.save(order);
        logger.info("Order updated successfully for order with ID: {}", orderId);
        return mapToOrderCreateResponse(order);
    }
}