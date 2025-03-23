package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.CreationNotificationRequest;
import com.example.courseapplicationproject.dto.request.MaskAllAsReadNotificationRequest;
import com.example.courseapplicationproject.dto.response.NotificationResponse;
import com.example.courseapplicationproject.service.NotificationService;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class NotificationController {
    NotificationService notificationService;

    @PostMapping
    public ApiResponse<Void> createNotification(@RequestBody CreationNotificationRequest request) {
        notificationService.createNotification(request);
        return ApiResponse.success(null, "Tạo thông báo thành công");
    }

    @PostMapping("/mark-all-as-read")
    public ApiResponse<Void> markAllAsRead(@RequestBody MaskAllAsReadNotificationRequest request) {
        notificationService.markAllAsRead(request);
        return ApiResponse.success(null, "Đánh dấu tất cả thông báo là đã đọc");
    }

    @GetMapping("/unread")
    public ApiResponse<List<NotificationResponse>> getUnreadNotifications() {
        List<NotificationResponse> unreadNotifications = notificationService.getUnreadNotificationsForUser();
        return ApiResponse.success(unreadNotifications, "Lấy danh sách thông báo chưa đọc thành công");
    }

    @DeleteMapping("/{notificationId}")
    public ApiResponse<Void> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ApiResponse.success(null, "Xóa thông báo thành công");
    }
}