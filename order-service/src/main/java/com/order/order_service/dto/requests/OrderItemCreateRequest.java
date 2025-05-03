package com.order.order_service.dto.requests;

import lombok.Data;

@Data
public class OrderItemCreateRequest {

    private String productId;
    private String productName;
    private Double productPrice;
    private Integer quantity;
    private Double discount;
    private String notes;
}
