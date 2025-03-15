package com.example.courseapplicationproject.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.courseapplicationproject.dto.request.OtpVerifyRequest;
import com.example.courseapplicationproject.dto.request.UserRequestCreation;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.UserResponse;
import com.example.courseapplicationproject.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/user")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserController {
    UserService userService;

    @PostMapping("/my-info")
    public ApiResponse<UserResponse> authenticate() {
        return ApiResponse.success(userService.myInfo(), "Get info successful");
    }

    @PostMapping("/sent-otp")
    public ApiResponse<Void> sentOtpRegister(@RequestBody @Valid UserRequestCreation request)
            throws MessagingException {
        userService.sentOtpRegister(request);
        return ApiResponse.success(null, "");
    }

    @PostMapping("/create")
    public ApiResponse<Void> createUser(@RequestBody @Valid OtpVerifyRequest request) throws MessagingException {
        userService.verifyAndCreateUser(request.getEmail(), request.getOtp());
        return ApiResponse.success(null, "");
    }
}
