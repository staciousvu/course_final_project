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
    LocalDateTime createdAt;
    String reviewerName;
    Integer rating;
    String review;
}
