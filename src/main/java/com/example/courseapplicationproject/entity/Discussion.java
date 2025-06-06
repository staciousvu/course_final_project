package com.example.courseapplicationproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Discussion extends AbstractEntity<Long> {
    @ManyToOne
    @JoinColumn(name = "lecture_id", nullable = false)
    private Lecture lecture;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)  // Thêm trường course
    private Course course;  // Thêm thuộc tính course

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @OneToMany(mappedBy = "discussion", cascade = CascadeType.ALL, orphanRemoval = true , fetch = FetchType.LAZY)
    private List<Reply> replies;
}
