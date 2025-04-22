package com.example.courseapplicationproject.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.example.courseapplicationproject.dto.event.NotificationEvent;
import jakarta.mail.MessagingException;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.courseapplicationproject.dto.event.MessageRabbbitMQ;
import com.example.courseapplicationproject.dto.request.*;
import com.example.courseapplicationproject.dto.response.OtpResponse;
import com.example.courseapplicationproject.dto.response.UserResponse;
import com.example.courseapplicationproject.entity.Role;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.mapper.UserMapper;
import com.example.courseapplicationproject.repository.RoleRepository;
import com.example.courseapplicationproject.repository.UserRepository;
import com.example.courseapplicationproject.service.interfaces.IUserService;
import com.example.courseapplicationproject.util.OtpUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService implements IUserService {
    static String suffixRegisterObject = "_object_register";
    static String suffixRegisterOtp = "_otp_register";
    static String suffixResetObject = "_object_reset";
    static String suffixResetOtp = "_otp_reset";
    static String company = "Eduflow";

//    @NonFinal
//    @Value("${rabbitmq.exchange-name}")
//    String exchangeName;
//
//    @NonFinal
//    @Value("${rabbitmq.routing-key}")
//    String routingKey;
    UserMapper userMapper;
    UserRepository userRepository;
    RedisService redisService;
//    RabbitTemplate rabbitTemplate;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    CloudinaryService cloudinaryService;
    KafkaTemplate<String,Object> kafkaTemplate;
    @Override
    @PreAuthorize("isAuthenticated()")
    public UserResponse myInfo() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.userToUserResponse(user);
    }
    @Override
    public void sentOtpRegister(UserRequestCreation request) throws MessagingException {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
//        if (!Objects.equals(request.getPassword(), request.getPasswordConfirm())) {
//            throw new AppException(ErrorCode.PASSWORD_CONFIRM_WRONG);
//        }

        redisService.save(request.getEmail() + suffixRegisterObject, request);
        String otp = OtpUtils.generateOtp();
        redisService.save(request.getEmail() + suffixRegisterOtp, otp);

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(request.getEmail())
                .templateCode("register-otp")
                .subject("Your OTP Code from " + company)
                .param(Map.of("otp", otp, "companyName", company))
                .build();

        kafkaTemplate.send("register", notificationEvent);
    }

    @Override
    public void verifyAndCreateUser(String email, String otp) {
        if (userRepository.existsByEmail(email)) throw new AppException(ErrorCode.USER_EXISTED);
        String storedOtp = redisService.get(email + suffixRegisterOtp, String.class);
        if (Objects.isNull(storedOtp)) throw new AppException(ErrorCode.EXPIRED_OTP);
        if (!storedOtp.equals(otp)) throw new AppException(ErrorCode.INVALID_OTP);
        UserRequestCreation request = redisService.get(email + suffixRegisterObject, UserRequestCreation.class);
        Role role = roleRepository
                .findByRoleName(Role.RoleType.LEARNER.toString())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        Role role2 = roleRepository
                .findByRoleName(Role.RoleType.INSTRUCTOR.toString())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        roles.add(role2);
        User user = User.builder()
                .roles(roles)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .isEnabled(true)
                .isTeacherApproved(false)
//                .isDeleted(false)
                .build();
//        userRepository.save(user);
    }

    @Override
    public void sentOtpReset(UserRequestReset userRequestReset) {
        if (!userRepository.existsByEmail(userRequestReset.getEmail())) throw new AppException(ErrorCode.USER_NOT_FOUND);

        redisService.save(userRequestReset.getEmail() + suffixResetObject, userRequestReset);
        String otp = OtpUtils.generateOtp();
        redisService.save(userRequestReset.getEmail() + suffixResetOtp, otp);

        NotificationEvent event = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient(userRequestReset.getEmail())
                .templateCode("reset-otp")
                .subject(company)
                .param(Map.of("otp", otp))
                .build();

        kafkaTemplate.send("reset-password", event);
    }

    @Override
    public void verifyOtpReset(VerifyResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        String storedOtp = redisService.get(request.getEmail() + suffixResetOtp, String.class);
        if (Objects.isNull(storedOtp) || !storedOtp.equals(request.getOtp()))
            throw new AppException(ErrorCode.INVALID_OTP);
        UserRequestReset request1 = redisService.get(request.getEmail() + suffixResetObject, UserRequestReset.class);
        user.setPassword(passwordEncoder.encode(request1.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!Objects.equals(request.getPassword(), request.getConfirmPassword()))
            throw new AppException(ErrorCode.PASSWORD_CONFIRM_WRONG);
        String storedOtp = redisService.get(request.getEmail() + suffixResetOtp, String.class);
        if (Objects.isNull(storedOtp) || !storedOtp.equals(request.getOtp()))
            throw new AppException(ErrorCode.INVALID_OTP);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        log.info("old password:"+user.getPassword());
        log.info("old password"+passwordEncoder.encode(request.getOldPassword()));
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new AppException(ErrorCode.OLD_PASSWORD_WRONG);
        if (!Objects.equals(request.getNewPassword(), request.getConfirmNewPassword()))
            throw new AppException(ErrorCode.PASSWORD_CONFIRM_WRONG);
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public UserResponse updateProfile(UpdateProfileRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        userMapper.updateUserFromUpdateProfileRequest(request, user);
        userRepository.save(user);
        return userMapper.userToUserResponse(user);
    }

    @Override
    public String uploadAvatar(MultipartFile file) throws IOException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getPublicId() != null && !user.getPublicId().isEmpty()) {
            cloudinaryService.deleteImage(user.getPublicId());
        }

        Map result = cloudinaryService.uploadImage(file);
        String avatar = result.get("secure_url").toString();
        String publicId = result.get("public_id").toString();

        user.setAvatar(avatar);
        user.setPublicId(publicId);
        userRepository.save(user);

        return avatar;
    }
}
