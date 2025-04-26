package com.order.order_service.dto.requests;

import lombok.Data;

@Data
public class OrderItemCreateRequest {

    private Long productId;
    private String productName;
    private Double productPrice;
    private Integer quantity;
    private Double discount;
    private String notes;
}
