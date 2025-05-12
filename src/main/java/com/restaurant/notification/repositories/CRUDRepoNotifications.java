package com.restaurant.notification.repositories;

import com.restaurant.notification.models.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CRUDRepoNotifications extends JpaRepository<Notifications, Long> {



}
