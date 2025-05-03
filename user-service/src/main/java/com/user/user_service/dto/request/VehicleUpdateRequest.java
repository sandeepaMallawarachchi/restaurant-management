package com.user.user_service.dto.request;

import lombok.Data;

@Data
public class VehicleUpdateRequest {
    private String vehicleNumber;
    private String vehicleImg;
    private String vehicleDocuments;
    private String licenseNumber;
}
