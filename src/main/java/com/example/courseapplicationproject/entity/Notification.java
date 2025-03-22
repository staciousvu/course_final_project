package com.example.courseapplicationproject.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "notification")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification extends AbstractEntity<Long> {
    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    String message;

    @Column(name = "start_at")
    LocalDateTime startTime;

    @Column(name = "end_at")
    LocalDateTime endTime;
}
