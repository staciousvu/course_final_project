package com.example.courseapplicationproject.dto.response;

import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PreferenceResponse {
    Long rootCategoryId;
    String rootCategoryName;
    List<SubCategoryResponse> subCategories;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SubCategoryResponse {
        Long id;
        String name;
    }
}
