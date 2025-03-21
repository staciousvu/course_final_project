package com.example.courseapplicationproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProgressResponse {
    String courseName;
    double percentage;
    int totalLecturesCompleted;
    int totalLectures;
    List<LecturesCompleted> lecturesCompleted;
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class LecturesCompleted {
        Long lectureId;
        String lectureName;
    }
}
