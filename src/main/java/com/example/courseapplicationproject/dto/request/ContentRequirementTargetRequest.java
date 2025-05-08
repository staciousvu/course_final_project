package com.example.courseapplicationproject.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContentRequirementTargetRequest {
    List<CourseContentDTO> contents;
    List<CourseRequirementDTO> requirements;
    List<CourseTargetDTO> targets;
}
