package com.example.courseapplicationproject.dto.response;

import jakarta.persistence.Column;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseDTO {
    Long id;
    String title;
    String subtitle;
    BigDecimal price;
    String description;
    String language;
    String level;
    Long categoryId;
    String previewUrl;
    String videoUrl;
    Double duration;
    Integer countEnrolled;
    Integer countRating;
    Double avgRating;
}

