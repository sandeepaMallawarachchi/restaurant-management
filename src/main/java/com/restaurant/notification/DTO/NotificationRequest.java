package com.restaurant.notification.DTO;

import lombok.Data;

@Data
public class NotificationRequest {
    private Long userId;
    private String title;
    private String message;
    private String email;
    private String type;
}
