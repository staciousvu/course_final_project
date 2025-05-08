package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.response.*;
import com.example.courseapplicationproject.service.InstructorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/instructor")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class InstructorController {
    InstructorService instructorService;
    @GetMapping("/all")
    public ApiResponse<List<InstructorListResponse>> getAllInstructor(@RequestParam(required = false) String keyword) {
        return ApiResponse.success(instructorService.getAllInstructor(keyword),"OK");
    }
    @GetMapping("/my-courses")
    public ApiResponse<Page<CourseResponse>> getMyCourses(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam String keyword
    ) {
        return ApiResponse.success(instructorService.instructorCourseResponses(page, size, keyword),"OK");
    }

}
