package com.example.courseapplicationproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "course_report")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseReport extends AbstractEntity<Long> {
    public enum ReportType{
        STUDENT,TEACHER
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Enumerated(EnumType.STRING)
    ReportType reportType;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    Course course;

    @Column(nullable = false)
    String reason;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ReportStatus status = ReportStatus.PENDING;

    public enum ReportStatus {
        PENDING,
        RESOLVED,
        REJECTED
    }
}
