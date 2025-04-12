package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.CourseContentDTO;
import com.example.courseapplicationproject.dto.request.CourseRequirementDTO;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.service.CourseContentService;
import com.example.courseapplicationproject.service.CourseRequirementService;
import com.example.courseapplicationproject.service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course-requirement")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CourseRequirementController {
    CourseRequirementService courseRequirementService;
    private final CourseService courseService;

    @GetMapping("/{courseId}")
    public ApiResponse<List<CourseRequirementDTO>> getAllCourseContent(@PathVariable Long courseId) {
        return ApiResponse.success(courseRequirementService.getAllRequirements(courseId),"OK");
    }
    @PostMapping("/{courseId}")
    public ApiResponse<Void> createRequirements(
            @PathVariable Long courseId,
            @RequestBody List<CourseRequirementDTO> requirements) {
        courseRequirementService.createRequirements(courseId, requirements);
        return ApiResponse.success(null,"OK");
    }

    @PutMapping("/{courseId}")
    public ApiResponse<Void> updateRequirements(
            @PathVariable Long courseId,
            @RequestBody List<CourseRequirementDTO> requirements) {
        courseRequirementService.updateRequirements(courseId, requirements);
        return ApiResponse.success(null,"OK");
    }

    @DeleteMapping("/{contentId}")
    public ApiResponse<Void> deleteRequirement(@PathVariable Long contentId) {
        courseRequirementService.deleteRequirement(contentId);
        return ApiResponse.success(null,"OK");
    }
}
