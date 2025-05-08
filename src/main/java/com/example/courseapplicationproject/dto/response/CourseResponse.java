package com.example.courseapplicationproject.dto.response;

import java.math.BigDecimal;
import java.util.List;

import com.example.courseapplicationproject.dto.request.CourseContentDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseResponse {
    Long id;
    String title;
    String subtitle;
    String description;
    BigDecimal price;
    BigDecimal discount_price;
    Integer duration;
    String language;
    String level;
    String thumbnail;
    String previewVideo;
    String authorName;
    String authorAvatar;
    String authorEmail;
    String categoryName;
    Double avgRating;
    Integer countRating;
    String status;
    Integer countEnrolled;
    String label;
    List<CourseContentDTO> contents;
}
