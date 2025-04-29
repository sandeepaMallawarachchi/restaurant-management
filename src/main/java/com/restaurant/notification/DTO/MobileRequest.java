package com.restaurant.notification.DTO;

import lombok.Data;

@Data
public class MobileRequest {
    private String toNumber;
    private String message;
}
