package com.restaurant.notification.controllers;

import com.restaurant.notification.DTO.MobileRequest;
import com.restaurant.notification.services.MobileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mobile")
public class MobileController {

    @Autowired
    private MobileService mobileService;


    @PostMapping("/send")
    public String sendSms(@RequestBody MobileRequest request) { mobileService.sendMobile(request.getToNumber(), request.getMessage()); return "SMS sent successfully!"; }

}
