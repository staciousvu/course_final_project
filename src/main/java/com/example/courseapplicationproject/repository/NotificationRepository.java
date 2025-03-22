package com.example.courseapplicationproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE NOT EXISTS "
            + "(SELECT nr FROM NotificationRead nr WHERE nr.notification = n AND nr.user.id = :userId)")
    List<Notification> findUnreadNotifications(@Param("userId") Long userId);
}
