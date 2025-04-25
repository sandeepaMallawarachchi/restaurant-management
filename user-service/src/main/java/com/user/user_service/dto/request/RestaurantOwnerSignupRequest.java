package com.user.user_service.dto.request;

import lombok.Data;

@Data
public class RestaurantOwnerSignupRequest {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
}
