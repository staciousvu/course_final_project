package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.UserResponse;
import com.example.courseapplicationproject.service.ProfileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ProfileController {
    ProfileService profileService;
    @GetMapping
    public ApiResponse<UserResponse> getProfile() {
        return ApiResponse.success(profileService.getProfile(),"OK");
    }
    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getProfile(@PathVariable Long userId) {
        return ApiResponse.success(profileService.getProfile(userId),"OK");
    }
}
