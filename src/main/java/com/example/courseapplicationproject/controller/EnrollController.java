package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.service.EnrollService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enrolls")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EnrollController {
    EnrollService enrollService;
    @GetMapping("/check/{instructorId}")
    public ApiResponse<Boolean> check(@PathVariable Long instructorId) {
        return ApiResponse.success(enrollService.checkStudentEnrolledCourseOfInstructor(instructorId),"OK");
    }
}
