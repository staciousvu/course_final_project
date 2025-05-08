package com.example.courseapplicationproject.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateFullQuizRequest {
    private Integer id;
    private String title;
    private String description;
    private Long courseId;
    private List<UpdateQuestion> questions;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class UpdateQuestion {
        private String content;
        private List<UpdateAnswer> answers;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class UpdateAnswer {
        private String content;
        private Boolean isCorrect;
    }
}