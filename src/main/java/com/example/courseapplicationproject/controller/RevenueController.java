package com.example.courseapplicationproject.controller;

import com.cloudinary.Api;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.RevenueInstructorResponse;
import com.example.courseapplicationproject.service.RevenueService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/revenue")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class RevenueController {
    RevenueService revenueService;
    @GetMapping("/{instructorId}")
    public ApiResponse<RevenueInstructorResponse> revenue(@PathVariable Long instructorId){
        return ApiResponse.success(revenueService.getRevenueInstructor(instructorId),"OK");
    }
}
