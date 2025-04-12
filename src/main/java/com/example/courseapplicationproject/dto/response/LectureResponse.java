package com.example.courseapplicationproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LectureResponse {
    Long id;
    String title;
    String type;
    String contentUrl;
    Double duration;
    Integer displayOrder;
    Long sectionId;
}
