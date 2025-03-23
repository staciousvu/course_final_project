package com.example.courseapplicationproject.dto.response;

import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecommendCourseCategoryLeafs {
    String categoryName;
    List<CourseResponse> courses;
}
