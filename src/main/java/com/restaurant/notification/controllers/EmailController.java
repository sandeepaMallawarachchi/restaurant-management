package com.restaurant.notification.controllers;

import com.restaurant.notification.DTO.EmailRequest;
import com.restaurant.notification.services.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.mail.javamail.JavaMailSender;


@RestController ("/email")
public class EmailController {

    private final JavaMailSender mailSender;


    @Value("${spring.mail.username}")
    private String FromEmail;

    private EmailService emailService;

    public EmailController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostMapping("/send")
    public String sendMail(@RequestBody EmailRequest request) { return emailService.EmailTransmitter(request); }

}
