package com.example.courseapplicationproject.dto.response;

import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryBasicResponse {
    Long id;
    String name;
    String description;
    String slug;
    Boolean isActive;
    Integer displayOrder;
    Long parentCategoryId;
    Set<CategoryBasicResponse> subCategories;
}
