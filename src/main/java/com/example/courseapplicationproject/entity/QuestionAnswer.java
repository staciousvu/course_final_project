package com.example.courseapplicationproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "question_answer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionAnswer extends AbstractEntity<Integer> {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id",nullable = false)
    QuizQuestion question;

    @Column(columnDefinition = "LONGTEXT")
    String content;

    @Column(name = "is_correct")
    Boolean isCorrect;
}
