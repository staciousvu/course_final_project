package com.example.courseapplicationproject.service.interfaces;

import java.util.List;

import com.example.courseapplicationproject.dto.request.CategoryRequest;
import com.example.courseapplicationproject.dto.response.CategoryBasicResponse;
import com.example.courseapplicationproject.dto.response.CategoryDetailResponse;

public interface ICategoryService {
    CategoryBasicResponse createCategory(CategoryRequest request);

    CategoryBasicResponse updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);

    CategoryDetailResponse getCategoryBySlug(String slug);

    List<CategoryBasicResponse> getAllCategories();

    List<CategoryBasicResponse> getActiveCategories();

    List<CategoryBasicResponse> searchCategories(String keyword);

    List<CategoryBasicResponse> getCategoriesByParentId(Long parentId);

    void updateDisplayOrder(Long id, int displayOrder);
}
