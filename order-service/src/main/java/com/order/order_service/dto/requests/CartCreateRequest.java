package com.order.order_service.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartCreateRequest {
    private Long userId;
    private Long restaurantId;
    private List<CartItemCreateRequest> cartItems;
}
