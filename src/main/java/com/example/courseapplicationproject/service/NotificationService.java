package com.example.courseapplicationproject.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.dto.request.CreationNotificationRequest;
import com.example.courseapplicationproject.dto.request.MaskAllAsReadNotificationRequest;
import com.example.courseapplicationproject.dto.response.NotificationResponse;
import com.example.courseapplicationproject.entity.Notification;
import com.example.courseapplicationproject.entity.NotificationRead;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.NotificationReadRepository;
import com.example.courseapplicationproject.repository.NotificationRepository;
import com.example.courseapplicationproject.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    NotificationRepository notificationRepository;
    NotificationReadRepository notificationReadRepository;
    UserRepository userRepository;

    public Notification createNotification(CreationNotificationRequest request) {
        Notification notification = Notification.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .startTime(request.getStartTime() != null ? request.getStartTime() : LocalDateTime.now())
                .endTime(request.getEndTime())
                .build();
        return notificationRepository.save(notification);
    }

    public void markAllAsRead(MaskAllAsReadNotificationRequest maskAllAsReadNotificationRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<Long> idsNotificationRead = maskAllAsReadNotificationRequest.getIdsNotificationRead();
        List<Notification> notifications = notificationRepository.findAllById(idsNotificationRead);
        List<NotificationRead> newReads = new ArrayList<>();

        for (Notification notification : notifications) {
            boolean alreadyRead = notificationReadRepository.existsByUserAndNotification(user, notification);
            if (!alreadyRead) {
                NotificationRead notificationRead = NotificationRead.builder()
                        .user(user)
                        .notification(notification)
                        .build();
                newReads.add(notificationRead);
            }
        }

        if (!newReads.isEmpty()) {
            notificationReadRepository.saveAll(newReads);
        }
    }

    public List<NotificationResponse> getUnreadNotificationsForUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Notification> notifications = notificationRepository.findUnreadNotifications(user.getId());

        return notifications.stream()
                .map(notification -> NotificationResponse.builder()
                        .id(notification.getId())
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .startTime(notification.getStartTime())
                        .endTime(notification.getEndTime())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new AppException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }
        notificationReadRepository.deleteByNotificationId(notificationId);
        notificationRepository.deleteById(notificationId);
    }
}
