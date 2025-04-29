package com.restaurant.notification.controllers;


import java.security.Principal;

import com.restaurant.notification.DTO.NotificationRequest;
import com.restaurant.notification.models.Notifications;
import com.restaurant.notification.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping ("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;


    @MessageMapping("/sendNotification")
    public void sendNotification( @Payload NotificationRequest request, Principal principal) { notificationService.SocketMessageTransmitter(request, principal); }

    @PostMapping
    public Notifications create (@RequestBody NotificationRequest request) { return notificationService.CreateNotification(request.getUserId(), request.getTitle(), request.getMessage(), request.getType()); }

    @DeleteMapping ("/delete")
    public void delete (@RequestParam("id") String Id) {
        notificationService.DeleteNotifications(Id);
    }

    @GetMapping
    public List<Notifications> getAll () {
        return notificationService.FetchAllNotifications();
    }

    @PutMapping ("/read")
    public Notifications markRead (@RequestParam("id") String Id) { return notificationService.UpdateReadStatus(Id); }

    @PutMapping ("/read-all")
    public List<Notifications> markReadAll () { return notificationService.updateReadStatusAll(); }

}