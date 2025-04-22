package com.example.courseapplicationproject.service.interfaces;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import jakarta.mail.MessagingException;

import org.springframework.web.multipart.MultipartFile;

import com.example.courseapplicationproject.dto.request.*;
import com.example.courseapplicationproject.dto.response.OtpResponse;
import com.example.courseapplicationproject.dto.response.UserResponse;

public interface IUserService {
    public UserResponse myInfo();

    public void sentOtpRegister(UserRequestCreation request) throws MessagingException;

    public void verifyAndCreateUser(String email, String otp);

    public void sentOtpReset(UserRequestReset request) throws MessagingException;

    public void verifyOtpReset(VerifyResetPasswordRequest request);

    public void resetPassword(ResetPasswordRequest request);

    public void changePassword(ChangePasswordRequest request);

    public UserResponse updateProfile(UpdateProfileRequest request);

    public String uploadAvatar(MultipartFile file) throws IOException;
}
