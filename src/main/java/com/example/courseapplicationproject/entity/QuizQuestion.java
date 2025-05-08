package com.example.courseapplicationproject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "quizz_question")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizQuestion extends AbstractEntity<Integer> {
    String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id",nullable = false)
    @JsonIgnore
    Quiz quiz;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "question",cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<QuestionAnswer> answers;
}
