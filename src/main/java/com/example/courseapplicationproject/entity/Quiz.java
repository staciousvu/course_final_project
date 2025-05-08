package com.example.courseapplicationproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizz")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Quiz extends AbstractEntity<Integer>{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id",nullable = false)
    @JsonIgnore
    Course course;

    String title;
    String description;
    Boolean isDeleted;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "quiz",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<QuizQuestion> quizQuestions = new ArrayList<>();
}
