package com.user.user_service.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleResponse {
    private Long id;
    private Long userId;
    private String vehicleNumber;
    private String vehicleType;
    private String vehicleImg;
    private String vehicleDocuments;
    private String licenseNumber;
}
