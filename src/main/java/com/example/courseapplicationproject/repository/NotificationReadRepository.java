package com.example.courseapplicationproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.Notification;
import com.example.courseapplicationproject.entity.NotificationRead;
import com.example.courseapplicationproject.entity.User;

@Repository
public interface NotificationReadRepository extends JpaRepository<NotificationRead, Long> {
    boolean existsByUserAndNotification(User user, Notification notification);

    void deleteByNotificationId(Long notificationId);
}
