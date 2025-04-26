package com.order.order_service.dto.requests;

import lombok.Data;

@Data
public class OrderLocationCreateRequest {

    private String address;
    private Double latitude;
    private Double longitude;
}
