package com.order.order_service.dto.responses;

import com.order.order_service.models.OrderStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderItemCreateResponse {
    private Long id;
    private String productId;
    private String productName;
    private Double productPrice;
    private Integer quantity;
    private Double discount;
    private String notes;
    private Double total;
}
