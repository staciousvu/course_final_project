package com.example.courseapplicationproject.entity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "course_progress")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseProgress extends AbstractEntity<Long> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    Lecture lecture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "is_completed", columnDefinition = "BOOLEAN DEFAULT FALSE")
    Boolean isCompleted;
}
