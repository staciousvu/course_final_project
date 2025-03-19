package com.example.courseapplicationproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

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
