package com.example.courseapplicationproject.dto.response;

import com.example.courseapplicationproject.dto.request.CourseContentDTO;
import com.example.courseapplicationproject.dto.request.CourseRequirementDTO;
import com.example.courseapplicationproject.dto.request.CourseTargetDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseDetailResponse {
    Long id;
    String title;
    String subtitle;
    String description;
    BigDecimal price;
    BigDecimal discount_price;
    Double duration;
    String language;
    String level;
    String thumbnail;
    String previewVideo;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    AuthorDTO author;
    List<CategoryDTO> categories;
    Double avgRating;
    Integer countRating;
    String status;
    Integer countEnrolled;
    String label;
    List<CourseContentDTO> contents;
    List<CourseRequirementDTO> requirements;
    List<CourseTargetDTO> targets;
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    static public class CategoryDTO{
        Long id;
        String categoryName;
    }
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    static public class AuthorDTO{
        Long id;
        String authorName;
        String authorAvatar;
        String bio;
        String expertise;
    }
}
