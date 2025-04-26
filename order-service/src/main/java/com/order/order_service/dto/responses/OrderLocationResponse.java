package com.order.order_service.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderLocationResponse {
    private Long id;
    private String address;
    private Double latitude;
    private Double longitude;
}
