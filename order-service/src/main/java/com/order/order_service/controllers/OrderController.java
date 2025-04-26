package com.order.order_service.controllers;

import com.order.order_service.dto.requests.OrderCreateRequest;
import com.order.order_service.dto.responses.OrderCreateResponse;
import com.order.order_service.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Page<OrderCreateResponse>> getAllOrderOfUser(
            @RequestAttribute("userId") Long userId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ){
        return ResponseEntity.ok(orderService.getAllOrderOfUser(userId, page, size));
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
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size
    ){
        return ResponseEntity.ok(orderService.getAll(page, size));
    }


}
