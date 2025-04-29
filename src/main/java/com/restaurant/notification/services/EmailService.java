package com.restaurant.notification.services;

import com.restaurant.notification.DTO.EmailRequest;
import com.restaurant.notification.controllers.EmailController;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String FromEmail;


    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public String EmailTransmitter (EmailRequest request) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(FromEmail);
            helper.setTo(request.getEmail());
            helper.setSubject(request.getSubject());

            try (var inputStream = EmailController.class.getResourceAsStream("/templates/email-template.html")) {
                if (inputStream == null) throw new RuntimeException("Template not found.");

                String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

                String content = template
                        .replace("{{title}}", request.getSubject())
                        .replace("{{message}}", request.getMessage());

                helper.setText(content, true);
            }

            mailSender.send(message);
            return "Email sent successfully!";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }

    }

}
