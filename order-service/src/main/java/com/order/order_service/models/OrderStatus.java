package com.order.order_service.models;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY_FOR_DELIVERY,
    OUT_FOR_DELIVERY,
    NEAR_BY,
    DELIVERED,
    CANCELLED
}
