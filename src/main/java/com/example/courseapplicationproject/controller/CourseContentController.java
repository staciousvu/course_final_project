package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.CourseContentDTO;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.service.CourseContentService;
import com.example.courseapplicationproject.service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course-content")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CourseContentController {
    CourseContentService courseContentService;
    private final CourseService courseService;

    @GetMapping("/{courseId}")
    public ApiResponse<List<CourseContentDTO>> getAllCourseContent(@PathVariable Long courseId) {
        return ApiResponse.success(courseContentService.getAllContents(courseId),"OK");
    }
    @PostMapping("/{courseId}")
    public ApiResponse<Void> createContents(
            @PathVariable Long courseId,
            @RequestBody List<CourseContentDTO> contents) {
        courseContentService.createContents(courseId, contents);
        return ApiResponse.success(null,"OK");
    }

    @PutMapping("/{courseId}")
    public ApiResponse<Void> updateContents(
            @PathVariable Long courseId,
            @RequestBody List<CourseContentDTO> contents) {
        courseContentService.updateContents(courseId, contents);
        return ApiResponse.success(null,"OK");
    }

    @DeleteMapping("/{contentId}")
    public ApiResponse<Void> deleteContent(@PathVariable Long contentId) {
        courseContentService.deleteContent(contentId);
        return ApiResponse.success(null,"OK");
    }
}
