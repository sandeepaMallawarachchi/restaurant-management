package com.user.user_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserSignupResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private int postalCode;
    private String phoneNumber;
    private Set<String> roles;
}
