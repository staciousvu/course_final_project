package com.example.courseapplicationproject.controller;

import java.util.List;

import com.example.courseapplicationproject.dto.response.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.courseapplicationproject.service.RecommendCourseService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RecommendCourseController {
    RecommendCourseService recommendCourseService;

    @GetMapping("/root")
    public ApiResponse<RecommendCourseCategoryRoot> getRecommendRoot() {
        return ApiResponse.success(recommendCourseService.getRecommendCoursesByPreferenceRoot(), "successful");
    }
    @GetMapping("/recommend-admin")
    public ApiResponse<List<RecommendCourseCategoryLeafs>> getRecommendAdmin() {
        return ApiResponse.success(recommendCourseService.getRecommendAdminCourse(), "successful");
    }

    @GetMapping("/leafs")
    public ApiResponse<List<RecommendCourseCategoryLeafs>> getRecommendLeafs() {
        return ApiResponse.success(recommendCourseService.getRecommendCoursesByLeafNodesCategory(), "successful");
    }
    @GetMapping("/activity")
    public ApiResponse<RecommendCourseCategoryRoot> getRecommendUserActivity() {
        return ApiResponse.success(recommendCourseService.getRecommendCoursesByUserActivity(), "successful");
    }
    @GetMapping("/keyword")
    public ApiResponse<List<RecommendCourseKeyword>> getRecommendKeyword() {
        return ApiResponse.success(recommendCourseService.getRecommendByUserSearchHistory(), "successful");
    }
    @GetMapping("/related-enrolled")
    public ApiResponse<List<CourseResponse>> getRecommendRelatedEnrolled() {
        return ApiResponse.success(recommendCourseService.getRecommendCoursesByRelatedCoursesEnrolled(), "successful");
    }
    @GetMapping("/category/{categoryId}")
    public ApiResponse<RecommendCourseCategoryRoot> getRecommendByCategory(@PathVariable Long categoryId) {
        return ApiResponse.success(recommendCourseService.recommendbyCategory(categoryId), "successful");
    }
}
