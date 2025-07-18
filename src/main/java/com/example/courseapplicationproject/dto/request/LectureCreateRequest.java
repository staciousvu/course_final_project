package com.example.courseapplicationproject.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LectureCreateRequest {
    String title;
    Integer displayOrder;
    Long sectionId;
    Long courseId;
}
