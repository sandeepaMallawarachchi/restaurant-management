package com.restaurant.notification.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailRequest {
    private String subject;
    private String message;
    private String email;
}
