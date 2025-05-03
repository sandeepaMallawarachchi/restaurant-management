package com.order.order_service.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CartResponse {
    private Long id;
    private Long userId;
    private String restaurantId;
    private Double total;
    private List<CartItemResponse> cartItems;
}
