package com.example.courseapplicationproject.dto.response;

import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecommendReferenceResponse {
    List<CourseResponse> coursesReferRoot;
    List<List<CourseResponse>> coursesReferChild;
}
