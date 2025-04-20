package com.order.order_service.dto.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Double price;
    private int quantity;
    private Double subtotal;
}
