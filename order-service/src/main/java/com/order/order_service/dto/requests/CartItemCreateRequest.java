package com.order.order_service.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemCreateRequest {
    private Long productId;
    private String productName;
    private Double price;
    private int quantity;
    private Long restaurantId;
}
