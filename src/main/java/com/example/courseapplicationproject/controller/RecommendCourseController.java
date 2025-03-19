package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.CourseResponse;
import com.example.courseapplicationproject.dto.response.PermissionResponse;
import com.example.courseapplicationproject.service.RecommendCourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RecommendCourseController {
    RecommendCourseService recommendCourseService;
    @GetMapping("/root")
    public ApiResponse<List<CourseResponse>> getRecommendRoot() {
        return ApiResponse.success(recommendCourseService.getRecommendCoursesByPreferenceRoot(), "successful");
    }
    @GetMapping("/leafs")
    public ApiResponse<List<List<CourseResponse>>> getRecommendLeafs() {
        return ApiResponse.success(recommendCourseService.getRecommendCoursesByLeafNodesCategory(), "successful");
    }
}
