package com.example.courseapplicationproject.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "course")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Course extends AbstractEntity<Long> {
    public enum CourseStatus {
        DRAFT,
        PENDING,
        ACCEPTED,
        REJECTED,
    }

    public enum LevelCourse {
        BEGINNER,
        INTERMEDIATE,
        EXPERT,
        ALL
    }

    @Column(name = "title", nullable = false, length = 255)
    String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    CourseStatus status;

    @Column(name = "subtitle", length = 500)
    String subtitle;

    @Column(name = "price", nullable = false)
    BigDecimal price;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    String description;

    @Column(name = "duration", nullable = false)
    Integer duration;

    @Column(name = "language", nullable = false)
    String language;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    LevelCourse level;

    @Column(name = "thumbnail")
    String thumbnail;

    @Column(name = "preview_video", nullable = false)
    String previewVideo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnore
    User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    Category category;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    Set<Section> sections = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    @JsonIgnore
    Set<CartItem> cartItems = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    @JsonIgnore
    Set<Enrollment> enrollments = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    @JsonIgnore
    Set<CourseReview> reviews = new HashSet<>();
}
