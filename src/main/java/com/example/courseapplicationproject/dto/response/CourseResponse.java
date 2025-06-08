package com.example.courseapplicationproject.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.courseapplicationproject.dto.request.CourseContentDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseResponse {
    Long id;
    String title;
    String subtitle;
    String description;
    BigDecimal price;
    BigDecimal discount_price;  
    Double duration;
    String language;
    String level;
    String thumbnail;
    String previewVideo;
    String authorName;
    Long authorId;
    String authorAvatar;
    String authorEmail;
    String categoryName;
    Double avgRating;
    Integer countRating;
    String isActive;
    String status;
    Integer countEnrolled;
    int totalVideo;
    String label;
    double progress;
    LocalDateTime updatedAt;
    LocalDateTime createdAt;
    List<CourseContentDTO> contents;
}
