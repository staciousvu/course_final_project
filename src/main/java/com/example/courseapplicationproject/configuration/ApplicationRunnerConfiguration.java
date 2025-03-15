package com.example.courseapplicationproject.configuration;

import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.courseapplicationproject.entity.Role;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.RoleRepository;
import com.example.courseapplicationproject.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class ApplicationRunnerConfiguration implements CommandLineRunner {
    RoleRepository roleRepository;
    UserRepository userRepository;
    String[] roleType = {"ADMIN", "INSTRUCTOR", "LEARNER"};

    @NonFinal
    @Value("${jwt.secret-key}")
    String secretKey;

    @Override
    public void run(String... args) throws Exception {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        log.info("ApplicationRunnerConfiguration");
        log.info("secretKey: {}", secretKey);
        Arrays.stream(roleType).forEach(role -> {
            if (!roleRepository.existsByRoleName(role)) {
                Role sub = Role.builder()
                        .roleName(role)
                        .description("ROLE : " + role)
                        .build();
                roleRepository.save(sub);
            }
        });
        Set<Role> roles = new HashSet<>();
        Role role = roleRepository
                .findByRoleName(Role.RoleType.ADMIN.toString())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        Role role1 = roleRepository
                .findByRoleName(Role.RoleType.LEARNER.toString())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        //        Permission
        roles.add(role);
        roles.add(role1);
        if (userRepository.findByEmail("vunguyen123@gmail.com").isEmpty()) {
            User user = User.builder()
                    .email("vunguyen123@gmail.com")
                    .password(passwordEncoder.encode("vunguyen123"))
                    .isEnabled(true)
                    .roles(roles)
                    .firstName("nguyen ba")
                    .lastName("vu")
                    .isTeacherApproved(false)
                    .build();
            userRepository.save(user);
            log.info("User saved vunguyen123@gmail.com");
        }
    }
}
