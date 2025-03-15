package com.example.courseapplicationproject.service.interfaces;

import java.io.IOException;

import jakarta.mail.MessagingException;

import org.springframework.web.multipart.MultipartFile;

import com.example.courseapplicationproject.dto.request.*;
import com.example.courseapplicationproject.dto.response.OtpResponse;
import com.example.courseapplicationproject.dto.response.UserResponse;

public interface IUserService {
    public UserResponse myInfo();

    public void sentOtpRegister(UserRequestCreation request) throws MessagingException;

    public void verifyAndCreateUser(String email, String otp);

    public void sentOtpReset(String email);

    public OtpResponse verifyOtpReset(VerifyResetPasswordRequest request);

    public void resetPassword(ResetPasswordRequest request);

    public void changePassword(ChangePasswordRequest request);

    public void updateProfile(UpdateProfileRequest request);

    public String uploadAvatar(MultipartFile file) throws IOException;
}
