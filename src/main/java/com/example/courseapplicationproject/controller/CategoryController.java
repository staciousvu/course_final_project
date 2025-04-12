package com.example.courseapplicationproject.controller;

import java.util.List;

import com.example.courseapplicationproject.dto.request.CategoryCreateRequest;
import com.example.courseapplicationproject.dto.response.*;
import com.example.courseapplicationproject.entity.Category;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import com.example.courseapplicationproject.dto.request.CategoryRequest;
import com.example.courseapplicationproject.service.CategoryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/categories")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class CategoryController {
    CategoryService categoryService;
    @PostMapping("/add-category")
    public ApiResponse<Void> addCategory(@RequestBody CategoryCreateRequest categoryCreateRequest) {
        categoryService.addCategory(categoryCreateRequest.getName(), categoryCreateRequest.getParentId());
        return ApiResponse.success(null,"OK");
    }
    @GetMapping("/id/{categoryId}")
    public ApiResponse<Category> getCategoryById(@PathVariable("categoryId") Long categoryId) {
        return ApiResponse.success(categoryService.getCategoryById(categoryId),"oke");
    }
//    @PostMapping("/create")
//    public ApiResponse<CategoryBasicResponse> createCategory(@RequestBody CategoryRequest request) {
//        return ApiResponse.success(categoryService.createCategory(request), "Category created successfully");
//    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryBasicResponse> updateCategory(
            @PathVariable Long id, @RequestBody CategoryRequest request) {
        return ApiResponse.success(categoryService.updateCategory(id, request), "Category updated successfully");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success(null, "Category deleted successfully");
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
    @PutMapping("/{id}/toggle-active")
    public ApiResponse<Void> toggleCategoryActive(@PathVariable Long id) {
        categoryService.toggleCategoryActive(id);
        return ApiResponse.success(null, "Category active status toggled successfully");
    }
    @GetMapping("/subcategories")
    public ApiResponse<List<CategoryDTO>> getSubcategories(@RequestParam Long parentId) {
        return ApiResponse.success(categoryService.getSubcategories(parentId),"OK");
    }
    @GetMapping("/hierarchy")
    public ApiResponse<List<CategoryDTO>> getCategoryHierarchy(@RequestParam Long topicId) {
        log.info(categoryService.getCategoryHierarchy(topicId).toString());
        return ApiResponse.success(categoryService.getCategoryHierarchy(topicId),"OK");
    }
    @GetMapping("/root")
    public ApiResponse<List<CategoryDTO>> getRootCategories() {
        return ApiResponse.success(categoryService.getRootCategories(), "OK");
    }
    @GetMapping("/survey/root")
    public ApiResponse<SurveyPrefRootResponse> getSurveyRootCategories() {
        return ApiResponse.success(categoryService.getSurveyPref(), "OK");
    }
    @GetMapping("/survey/topic/{parentId}")
    public ApiResponse<SurveyPrefTopicResponse> getSurveyTopicCategories(@PathVariable Long parentId) {
        return ApiResponse.success(categoryService.surveyPrefTopicResponse(parentId), "OK");
    }




}
