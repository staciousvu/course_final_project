package com.example.courseapplicationproject.service.interfaces;

import com.example.courseapplicationproject.dto.request.AuthenticationRequest;
import com.example.courseapplicationproject.dto.request.IntrospectRequest;
import com.example.courseapplicationproject.dto.response.AuthenticationResponse;
import com.example.courseapplicationproject.dto.response.IntrospectResponse;
import com.example.courseapplicationproject.entity.User;

public interface IAuthenticateService {
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);

    public String generateToken(User user);

    public IntrospectResponse introspectToken(IntrospectRequest introspectRequest);

    public String buildScope(User user);
}
