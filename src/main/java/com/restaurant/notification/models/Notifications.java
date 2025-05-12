package com.restaurant.notification.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String message;
    private String title;
    private LocalDateTime timestamp;
    private boolean isRead;
    private String type;


    public Notifications(Long userId, String title, String message, String type) {
        this.userId = userId;
        this.message = message;
        this.title = title;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
        this.type = type;
    }
}
