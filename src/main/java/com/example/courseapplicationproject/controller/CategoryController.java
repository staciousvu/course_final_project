package com.example.courseapplicationproject.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.courseapplicationproject.dto.request.CategoryRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.CategoryBasicResponse;
import com.example.courseapplicationproject.dto.response.CategoryDetailResponse;
import com.example.courseapplicationproject.service.CategoryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/categories")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CategoryController {
    CategoryService categoryService;

    @PostMapping
    public ApiResponse<CategoryBasicResponse> createCategory(@RequestBody CategoryRequest request) {
        return ApiResponse.success(categoryService.createCategory(request), "Category created successfully");
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryBasicResponse> updateCategory(
            @PathVariable Long id, @RequestBody CategoryRequest request) {
        return ApiResponse.success(categoryService.updateCategory(id, request), "Category updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success(null, "Category soft deleted successfully");
    }

    @GetMapping("/{slug}")
    public ApiResponse<CategoryDetailResponse> getCategoryBySlug(@PathVariable String slug) {
        return ApiResponse.success(categoryService.getCategoryBySlug(slug), "Category fetched successfully");
    }

    @GetMapping
    public ApiResponse<List<CategoryBasicResponse>> getAllCategories() {
        return ApiResponse.success(categoryService.getAllCategories(), "All categories fetched successfully");
    }

    @GetMapping("/active")
    public ApiResponse<List<CategoryBasicResponse>> getActiveCategories() {
        return ApiResponse.success(categoryService.getActiveCategories(), "Active categories fetched successfully");
    }

    @GetMapping("/search")
    public ApiResponse<List<CategoryBasicResponse>> searchCategories(@RequestParam String keyword) {
        return ApiResponse.success(categoryService.searchCategories(keyword), "Categories searched successfully");
    }

    @GetMapping("/parent/{parentId}")
    public ApiResponse<List<CategoryBasicResponse>> getCategoriesByParentId(@PathVariable Long parentId) {
        return ApiResponse.success(
                categoryService.getCategoriesByParentId(parentId), "Categories by parent fetched successfully");
    }

    @PutMapping("/{id}/display-order")
    public ApiResponse<Void> updateDisplayOrder(@PathVariable Long id, @RequestParam int displayOrder) {
        categoryService.updateDisplayOrder(id, displayOrder);
        return ApiResponse.success(null, "Category display order updated successfully");
    }
}
