package com.example.courseapplicationproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentEnrollmentDTO {
    private Long userId;
    private String fullName;
    private String email;
    private Long enrollmentId;
    private LocalDateTime enrolledOn;
    private Long courseId;
    private String courseTitle;
    private Long lessonsCompleted;
    private Long totalLessons;
    private Boolean isCompleted;
    private Integer rating;
    private Long questions;
}
