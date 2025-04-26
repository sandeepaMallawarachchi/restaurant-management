package com.order.order_service.controllers;

import com.order.order_service.dto.requests.OrderCreateRequest;
import com.order.order_service.dto.requests.OrderFilterRequest;
import com.order.order_service.dto.responses.OrderCreateResponse;
import com.order.order_service.models.OrderStatus;
import com.order.order_service.models.PaymentMethod;
import com.order.order_service.models.PaymentStatus;
import com.order.order_service.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @PostMapping
    public ResponseEntity<OrderCreateResponse> createOrder(
            @RequestAttribute("userId") Long userId,
            @RequestBody OrderCreateRequest request
    ){
        return ResponseEntity.ok(orderService.createOrder(request, userId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Page<OrderCreateResponse>> getAllOrderOfUser(
            @RequestAttribute("userId") Long userId,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(required = false) LocalDateTime orderDateStart,
            @RequestParam(required = false) LocalDateTime orderDateEnd,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ){
        PaymentStatus paymentStatus1 = null;
        PaymentMethod paymentMethod1 = null;
        OrderStatus orderStatus1 = null;
        if (paymentStatus != null) {
            paymentStatus1 = PaymentStatus.valueOf(paymentStatus.toUpperCase());
        }
        if (paymentMethod != null) {
            paymentMethod1 = PaymentMethod.valueOf(paymentMethod.toUpperCase());
        }
        if (orderStatus != null) {
            orderStatus1 = OrderStatus.valueOf(orderStatus.toUpperCase());
        }
        OrderFilterRequest orderFilterRequest = mapToOrderFilterRequest(
                userId,orderStatus1,restaurantId,orderDateStart,orderDateEnd,paymentMethod1,
                paymentStatus1,page,size
        );
        return ResponseEntity.ok(orderService.getAllOrder(orderFilterRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderCreateResponse> getOrder(
            @RequestAttribute("userId") Long userId,
            @PathVariable("id") Long orderId
    ){
        return ResponseEntity.ok(orderService.getOrder(orderId, userId));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<OrderCreateResponse>> getAll(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) Long restaurantId,
            @RequestParam(required = false) LocalDateTime orderDateStart,
            @RequestParam(required = false) LocalDateTime orderDateEnd,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ){
        PaymentStatus paymentStatus1 = null;
        PaymentMethod paymentMethod1 = null;
        OrderStatus orderStatus1 = null;
        if (paymentStatus != null) {
            paymentStatus1 = PaymentStatus.valueOf(paymentStatus.toUpperCase());
        }
        if (paymentMethod != null) {
            paymentMethod1 = PaymentMethod.valueOf(paymentMethod.toUpperCase());
        }
        if (orderStatus != null) {
            orderStatus1 = OrderStatus.valueOf(orderStatus.toUpperCase());
        }
        OrderFilterRequest orderFilterRequest = mapToOrderFilterRequest(
                userId,orderStatus1,restaurantId,orderDateStart,orderDateEnd,paymentMethod1,
                paymentStatus1,page,size
        );
        return ResponseEntity.ok(orderService.getAllOrder(orderFilterRequest));
    }

    @GetMapping("/restaurant/{id}")
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER')")
    public ResponseEntity<Page<OrderCreateResponse>> getAllOrdersOfRestaurant(
            @RequestParam(required = false) Long userId,
            @PathVariable("id") Long restaurantId,
            @RequestParam(required = false) String orderStatus,
            @RequestParam(required = false) LocalDateTime orderDateStart,
            @RequestParam(required = false) LocalDateTime orderDateEnd,
            @RequestParam(required = false) String paymentMethod,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ){
        PaymentStatus paymentStatus1 = null;
        PaymentMethod paymentMethod1 = null;
        OrderStatus orderStatus1 = null;
        if (paymentStatus != null) {
            paymentStatus1 = PaymentStatus.valueOf(paymentStatus.toUpperCase());
        }
        if (paymentMethod != null) {
            paymentMethod1 = PaymentMethod.valueOf(paymentMethod.toUpperCase());
        }
        if (orderStatus != null) {
            orderStatus1 = OrderStatus.valueOf(orderStatus.toUpperCase());
        }
        OrderFilterRequest orderFilterRequest = mapToOrderFilterRequest(
                userId,orderStatus1,restaurantId,orderDateStart,orderDateEnd,paymentMethod1,
                paymentStatus1,page,size
        );
        return ResponseEntity.ok(orderService.getAllOrder(orderFilterRequest));
    }

    private OrderFilterRequest mapToOrderFilterRequest(
            Long userId,
            OrderStatus orderStatus,
            Long restaurantId,
            LocalDateTime orderDateStart,
            LocalDateTime orderDateEnd,
            PaymentMethod paymentMethod,
            PaymentStatus paymentStatus,
            Integer page,
            Integer size
    ){
        return OrderFilterRequest.builder()
                .userId(userId)
                .orderStatus(orderStatus)
                .restaurantId(restaurantId)
                .orderDateStart(orderDateStart)
                .orderDateEnd(orderDateEnd)
                .paymentMethod(paymentMethod)
                .paymentStatus(paymentStatus)
                .page(page)
                .size(size)
                .build();
    }

}
