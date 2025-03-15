package com.example.courseapplicationproject.entity;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "lecture")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lecture extends AbstractEntity<Long> {
    public enum LectureType {
        VIDEO,
        FILE,
        URL
    }

    @Column(name = "title", nullable = false)
    String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    LectureType type;

    @Column(name = "content_url")
    String contentUrl;

    @Column(name = "duration", nullable = false)
    Integer duration;

    @Column(name = "display_order")
    Integer displayOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    @JsonIgnore
    Section section;
}
