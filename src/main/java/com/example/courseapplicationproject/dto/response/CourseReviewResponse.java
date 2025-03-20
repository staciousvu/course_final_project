package com.example.courseapplicationproject.dto.response;

import com.example.courseapplicationproject.entity.Course;
import com.example.courseapplicationproject.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

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
