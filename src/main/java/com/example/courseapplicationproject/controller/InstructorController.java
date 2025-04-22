package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.response.AdminDTO;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.InstructorCourseResponse;
import com.example.courseapplicationproject.dto.response.InstructorListResponse;
import com.example.courseapplicationproject.service.InstructorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
    public ApiResponse<List<InstructorCourseResponse>> getMyCourses() {
        return ApiResponse.success(instructorService.instructorCourseResponses(),"OK");
    }

}
