package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.*;
import com.example.courseapplicationproject.dto.response.OtpResponse;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.UserResponse;
import com.example.courseapplicationproject.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/user")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class UserController {
    UserService userService;

    @GetMapping("/my-info")
    public ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.success(userService.myInfo(), "Lấy thông tin thành công");
    }
    @GetMapping("/info/{id}")
    public ApiResponse<UserResponse> getInfoById(@PathVariable Long id) {
        return ApiResponse.success(userService.getInfoById(id), "Lấy thông tin thành công");
    }

    @PostMapping("/sent-otp")
    public ApiResponse<Void> sentOtpRegister(@RequestBody @Valid UserRequestCreation request)
            throws MessagingException {
        userService.sentOtpRegister(request);
        return ApiResponse.success(null, "OTP đã được gửi");
    }

    @PostMapping("/create")
    public ApiResponse<Void> createUser(@RequestBody @Valid OtpVerifyRequest request) {
        userService.verifyAndCreateUser(request.getEmail(), request.getOtp());
        return ApiResponse.success(null, "Tài khoản đã được tạo");
    }

    @PostMapping("/sent-otp-reset")
    public ApiResponse<Void> sentOtpReset(@RequestBody UserRequestReset userRequestReset) {
        userService.sentOtpReset(userRequestReset);
        return ApiResponse.success(null, "OTP đặt lại mật khẩu đã được gửi");
    }

    @PostMapping("/verify-otp-reset")
    public ApiResponse<Void> verifyOtpReset(@RequestBody @Valid VerifyResetPasswordRequest request) {
        userService.verifyOtpReset(request);
        return ApiResponse.success(null, "Xác thực OTP thành công");
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ApiResponse.success(null, "Mật khẩu đã được đặt lại");
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(request);
        return ApiResponse.success(null, "Mật khẩu đã được thay đổi");
    }
    @PutMapping("/update-profile")
    public ApiResponse<UserResponse> updateProfile(@RequestBody @Valid UpdateProfileRequest request) {
        return ApiResponse.success(userService.updateProfile(request), "Cập nhật hồ sơ thành công");
    }
    @PostMapping("/upload-avatar")
    public ApiResponse<String> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        String avatarUrl = userService.uploadAvatar(file);
        return ApiResponse.success(avatarUrl, "Ảnh đại diện đã được cập nhật");
    }
}
