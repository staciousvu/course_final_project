package com.example.courseapplicationproject.dto.projection;

import java.time.LocalDateTime;

public interface StudentEnrollmentProjection {
    String getThumbnail();
    Long getUserId();
    String getFullName();
    String getEmail();
    Long getEnrollmentId();
    LocalDateTime getEnrolledOn();
    Long getCourseId();
    String getCourseTitle();
    Long getLessonsCompleted();
    Long getTotalLessons();
    Boolean getIsCompleted();
    Integer getRating();
    Long getQuestions();
    // Getter cho progress, tính toán từ lessonsCompleted và totalLessons
    default double getProgress() {
        return getTotalLessons() > 0 ? (getLessonsCompleted() * 100.0 / getTotalLessons()) : 0.0;
    }
}
