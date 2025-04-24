package com.user.user_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminSignupRequest {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
}
