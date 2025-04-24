package com.user.user_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryPersonSignupRequest {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private int postalCode;
    private String phoneNumber;
    private String vehicleNumber;
    private String vehicleType;
    private String vehicleImg;
    private String profileImg;
    private String nic;
    private String vehicleDocuments;
    private String licenseNumber;
}
