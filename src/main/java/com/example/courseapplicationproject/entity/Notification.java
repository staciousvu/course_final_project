package com.example.courseapplicationproject.entity;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    String message;

    @Column(name = "is_read", columnDefinition = "BOOLEAN DEFAULT FALSE")
    Boolean isRead;

    @Column(name = "link_url")
    String linkUrl;
}
