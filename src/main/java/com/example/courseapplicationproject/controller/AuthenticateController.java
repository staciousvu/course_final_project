package com.example.courseapplicationproject.controller;

import java.text.ParseException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.courseapplicationproject.dto.request.AuthenticationRequest;
import com.example.courseapplicationproject.dto.request.IntrospectRequest;
import com.example.courseapplicationproject.dto.request.RefreshRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.AuthenticationResponse;
import com.example.courseapplicationproject.dto.response.IntrospectResponse;
import com.example.courseapplicationproject.service.AuthenticateService;
import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class AuthenticateController {
    AuthenticateService authenticateService;

    @PostMapping("/log-in")
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        return ApiResponse.success(authenticateService.authenticate(authenticationRequest), "Login successful");
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest introspectRequest) {
        return ApiResponse.success(authenticateService.introspectToken(introspectRequest), "Introspect successful");
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {
        var result = authenticateService.refreshToken(request);
        return ApiResponse.success(result, "Refresh successful");
    }

}
