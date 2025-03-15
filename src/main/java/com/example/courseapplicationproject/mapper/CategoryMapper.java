package com.example.courseapplicationproject.mapper;

import org.mapstruct.Mapper;

import com.example.courseapplicationproject.dto.request.CategoryRequest;
import com.example.courseapplicationproject.dto.response.CategoryBasicResponse;
import com.example.courseapplicationproject.dto.response.CategoryDetailResponse;
import com.example.courseapplicationproject.elasticsearch.document.CategoryDocument;
import com.example.courseapplicationproject.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryBasicResponse toCategoryResponse(Category category);

    Category toCategory(CategoryRequest categoryRequest);

    CategoryDetailResponse toCategoryDetailResponse(Category category);

    CategoryDocument toCategoryDocument(Category category);

    CategoryBasicResponse toCategoryBasicResponse(CategoryDocument categoryDocument);
}
