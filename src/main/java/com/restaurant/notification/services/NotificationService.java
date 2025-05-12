package com.restaurant.notification.services;

import com.restaurant.notification.DTO.EmailRequest;
import com.restaurant.notification.DTO.NotificationRequest;
import com.restaurant.notification.models.Notifications;
import com.restaurant.notification.repositories.CRUDRepoNotifications;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class NotificationService {

    @Autowired
    private CRUDRepoNotifications CRUDRepo;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private EmailService emailService;


    public Notifications CreateNotification (Long UserId, String title, String message, String type) {
        return CRUDRepo.save(new Notifications(UserId, title, message, type));
    }

    public void DeleteNotifications (String Id) {
        CRUDRepo.deleteById(Long.valueOf(Id));
    }

    public Notifications UpdateReadStatus (String Id) {
        Notifications notification = CRUDRepo.findById(Long.valueOf(Id))
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        return CRUDRepo.save(notification);
    }

    public List<Notifications> updateReadStatusAll() {
        List<Notifications> notifications = CRUDRepo.findAll();
        for (Notifications notification : notifications) {
            notification.setRead(true);
        }
        return CRUDRepo.saveAll(notifications);
    }


    public List<Notifications> FetchAllNotifications () {
        return CRUDRepo.findAll();
    }

    public void SocketMessageTransmitter (NotificationRequest request, Principal principal) {

        if (principal == null) {
            throw new AuthenticationCredentialsNotFoundException("Unauthenticated");
        }

        String authenticatedUserId = principal.getName();
        try {
            Long.parseLong(authenticatedUserId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid user ID format");
        }

        if (!authenticatedUserId.equals(String.valueOf(request.getUserId()))) {
            log.warn("Security alert: User {} attempted to act as {}",
                    authenticatedUserId, request.getUserId());
            throw new SecurityException("User impersonation attempt");
        }

        try {
            Map<String, Object> headers = new HashMap<>();
            headers.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSendToUser(
                    authenticatedUserId,
                    "/queue/notifications",
                    request,
                    headers
            );

            // send update via email
            EmailRequest emailRequest = new EmailRequest(request.getTitle(), request.getMessage(), request.getEmail());
            emailService.EmailTransmitter(emailRequest);

            log.info("Notification sent to user {}", authenticatedUserId);
        } catch (Exception e) {
            log.error("Failed to send notification", e);
            throw new MessageDeliveryException("Notification delivery failed");
        }

    }

}
