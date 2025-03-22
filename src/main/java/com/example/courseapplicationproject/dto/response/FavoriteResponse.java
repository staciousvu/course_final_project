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
public class FavoriteResponse {
    @Builder.Default
    List<CourseFavorite> favorites = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CourseFavorite {
        CourseResponse courseResponse;
        double totalHour;
        int totalLectures;
    }
}
