package com.example.courseapplicationproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryRecommendAdminDTO {
    List<RootCategoriesDTO> rootCategoriesDTOS = new ArrayList<RootCategoriesDTO>();

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RootCategoriesDTO{
        Long id;
        String name;
        String slug;
        Boolean isActive;
        Integer displayOrder;
        List<LeafCategoriesDTO> leafCategories=new ArrayList<LeafCategoriesDTO>();
    }
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class LeafCategoriesDTO{
        Long id;
        String name;
        String slug;
        Long totalCourses;
        Boolean isActive;
        Integer displayOrder;
    }
}
