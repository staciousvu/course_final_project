package com.example.courseapplicationproject.dto.response;

import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDetailResponse {
    Long id;
    String name;
    String description;
    String slug;
    Boolean isActive;
    Integer displayOrder;
    Long parentCategoryId;
    Set<CategoryBasicResponse> subCategories;
    private Integer studentCount;
    private Integer courseCount;
}
