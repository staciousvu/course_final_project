package com.example.courseapplicationproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuizByIdResponse {
    Integer id;
    String title;
    String description;
    Long courseId;
    List<QuestionResponse> questions;
    LocalDateTime createdAt;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class QuestionResponse {
        Integer id;
        String content;
        List<AnswerResponse> answers;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class AnswerResponse {
        Integer id;
        String content;
        Boolean isCorrect;
    }
}
