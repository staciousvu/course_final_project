package com.example.courseapplicationproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseReportResponse {
    private Long id;

    // Thông tin người dùng
    private String userEmail;
    private String userAvatar;
    private String userFullName;

    // Thông tin khóa học
    private String courseTitle;
    private Long courseId;
    private String courseThumbnail;
    private String authorFullName;

    // Nội dung báo cáo
    private String reason;
    private String description;
    private String imageUrl;
    private String status;

    private LocalDateTime createdAt;
}
