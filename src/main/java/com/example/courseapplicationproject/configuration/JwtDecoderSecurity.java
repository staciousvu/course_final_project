package com.example.courseapplicationproject.configuration;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
public class JwtDecoderSecurity implements JwtDecoder {
    @Override
    public Jwt decode(String token) throws JwtException {
        return null;
    }
}
