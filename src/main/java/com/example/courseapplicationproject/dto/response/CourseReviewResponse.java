package com.example.courseapplicationproject.dto.response;

import java.time.LocalDateTime;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseReviewResponse {
    Long id;
    String courseName;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String reviewerName;
    String reviewerAvatar;
    Integer rating;
    String review;
}
