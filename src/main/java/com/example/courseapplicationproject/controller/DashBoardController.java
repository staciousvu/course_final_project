package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.projection.PerformanceOverviewProjection;
import com.example.courseapplicationproject.dto.projection.StudentEnrollmentProjection;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.service.DashBoardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class DashBoardController {
    DashBoardService dashBoardService;
    @GetMapping("/performance-student")
    public ApiResponse<Page<StudentEnrollmentProjection>> performanceStudent(
            @RequestParam(value = "courseId", required = false) Long courseId,
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "status", defaultValue = "All") String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Page<StudentEnrollmentProjection> students = dashBoardService.getStudents(
                courseId, search, status, page, size);
        return ApiResponse.success(students,"OK");
    }
    @GetMapping("/performance-overview")
    public ApiResponse<List<PerformanceOverviewProjection>> performanceOverview(
            @RequestParam(value = "courseId", required = false) Long courseId,
            @RequestParam(value = "days") int days,
            @RequestParam(value = "months") int months )
            {
        return ApiResponse.success(dashBoardService.getTeacherRevenue(days),"OK");
    }
}
