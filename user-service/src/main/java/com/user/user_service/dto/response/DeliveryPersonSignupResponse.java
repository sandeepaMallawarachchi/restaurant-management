package com.user.user_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class DeliveryPersonSignupResponse {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private int postalCode;
    private String phoneNumber;
    private String profileImg;
    private String nic;
    private Set<String> roles;
    private VehicleResponse vehicle;
}
