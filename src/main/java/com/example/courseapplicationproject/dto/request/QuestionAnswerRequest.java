package com.example.courseapplicationproject.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QuestionAnswerRequest {
    Integer answerId; // dùng khi update
    Integer questionId;
    String content;
    Boolean isCorrect;
}
