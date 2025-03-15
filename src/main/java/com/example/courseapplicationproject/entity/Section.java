package com.example.courseapplicationproject.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "section")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Section extends AbstractEntity<Long> {
    @Column(name = "title", nullable = false, length = 255)
    String title;

    @Column(name = "display_order", columnDefinition = "INT DEFAULT 0")
    Integer displayOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    @JsonIgnore
    Course course;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    Set<Lecture> lectures = new HashSet<>();
}
