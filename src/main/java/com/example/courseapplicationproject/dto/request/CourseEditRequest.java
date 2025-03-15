package com.example.courseapplicationproject.dto.request;

import java.math.BigDecimal;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseEditRequest {
    String title;
    String subtitle;
    BigDecimal price;
    String description;
    Integer duration;
    String language;
    String level;
    Long categoryId;
}
