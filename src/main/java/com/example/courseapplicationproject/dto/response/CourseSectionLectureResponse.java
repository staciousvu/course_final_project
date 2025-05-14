package com.example.courseapplicationproject.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseSectionLectureResponse {
    Long courseId;
    String courseName;
    Long authorId;
    int totalSections;
    int totalLectures;
    Double duration;
    ProgressResponse progressResponse;

    @Builder.Default
    List<SectionResponse> sections = new ArrayList<SectionResponse>();

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SectionResponse {
        Long id;
        String title;
        String description;
        int displayOrder;
        int totalLectures;

        @Builder.Default
        List<LectureResponse> lectures = new ArrayList<LectureResponse>();
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class LectureResponse {
        Long id;
        String title;
        String type;
        String contentUrl;
        int displayOrder;
        boolean isCompleted;
        Double duration;
    }
}
