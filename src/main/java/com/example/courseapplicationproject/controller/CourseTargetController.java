package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.CourseTargetDTO;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.service.CourseTargetService;
import com.example.courseapplicationproject.service.CourseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course-target")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CourseTargetController {
    CourseTargetService courseTargetService;
    CourseService courseService;

    @GetMapping("/{courseId}")
    public ApiResponse<List<CourseTargetDTO>> getAllCourseTargets(@PathVariable Long courseId) {
        return ApiResponse.success(courseTargetService.getAllTargets(courseId), "OK");
    }

    @PostMapping("/{courseId}")
    public ApiResponse<Void> createTargets(
            @PathVariable Long courseId,
            @RequestBody List<CourseTargetDTO> targets) {
        courseTargetService.createTargets(courseId, targets);
        return ApiResponse.success(null, "OK");
    }

    @PutMapping("/{courseId}")
    public ApiResponse<Void> updateTargets(
            @PathVariable Long courseId,
            @RequestBody List<CourseTargetDTO> targets) {
        courseTargetService.updateTargets(courseId, targets);
        return ApiResponse.success(null, "OK");
    }

    @DeleteMapping("/{targetId}")
    public ApiResponse<Void> deleteTarget(@PathVariable Long targetId) {
        courseTargetService.deleteTarget(targetId);
        return ApiResponse.success(null, "OK");
    }
}
