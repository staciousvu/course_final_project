package com.example.courseapplicationproject.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.dto.request.AuthenticationRequest;
import com.example.courseapplicationproject.dto.request.IntrospectRequest;
import com.example.courseapplicationproject.dto.request.RefreshRequest;
import com.example.courseapplicationproject.dto.response.AuthenticationResponse;
import com.example.courseapplicationproject.dto.response.IntrospectResponse;
import com.example.courseapplicationproject.entity.InvalidatedToken;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.InvalidatedTokenRepository;
import com.example.courseapplicationproject.repository.UserRepository;
import com.example.courseapplicationproject.service.interfaces.IAuthenticateService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class AuthenticateService implements IAuthenticateService {
    @NonFinal
    @Value("${jwt.secret-key}")
    protected String SECRET_KEY;

    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = userRepository
                .findByEmail(authenticationRequest.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_EXISTED));
        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }
        if (!user.getIsEnabled()) throw new AppException(ErrorCode.ACCOUNT_BANNED);
        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(generateToken(user))
                .build();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());
        boolean valid = signedJWT.verify(verifier);
        Date expiredTime = isRefresh
                ? Date.from(signedJWT
                        .getJWTClaimsSet()
                        .getIssueTime()
                        .toInstant()
                        .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS))
                : signedJWT.getJWTClaimsSet().getExpirationTime();
        if (!valid || expiredTime.before(new Date())) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.AUTHENTICATION_FAILED);
        }
        return signedJWT;
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        String token = request.getToken();
        SignedJWT signedJWT = verifyToken(token, true);
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(signedJWT.getJWTClaimsSet().getJWTID())
                .expiryDate(new Date())
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
        User user = userRepository
                .findByEmail(signedJWT.getJWTClaimsSet().getSubject())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return AuthenticationResponse.builder()
                .token(generateToken(user))
                .authenticated(true)
                .build();
    }

    @Override
    public String generateToken(User user) {
        JWSAlgorithm algorithm = JWSAlgorithm.HS256;
        JWSHeader jwsHeader = new JWSHeader(algorithm);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .claim("scope", buildScope(user))
                .issueTime(new Date())
                .issuer("stacious-vu")
                .expirationTime(Date.from(Instant.now().plus(60, ChronoUnit.MINUTES)))
                .jwtID(UUID.randomUUID().toString())
                .claim("username", user.getEmail())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            MACSigner macSigner = new MACSigner(SECRET_KEY.getBytes());
            jwsObject.sign(macSigner);
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Can not generate token", e);
            throw new AppException(ErrorCode.GENERATE_TOKEN_FAILED);
        }
    }

    @Override
    public IntrospectResponse introspectToken(IntrospectRequest introspectRequest) {
        String token = introspectRequest.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (JOSEException | ParseException | AppException e) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }

    @Override
    public String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        Optional.ofNullable(user.getRoles()).ifPresent(roles -> {
            roles.forEach(role -> {
                stringJoiner.add(role.getRoleName());
                Optional.ofNullable(role.getPermissions()).ifPresent(permissions -> {
                    permissions.forEach(permission -> {
                        stringJoiner.add(permission.getPermissionName());
                    });
                });
            });
        });
        log.info("string joiner" + stringJoiner.toString());
        return stringJoiner.toString();
    }
}
// example @postauthorize
// Chạy trong phương thức rồi mới kiểm tra thay vì kiểm tra trước r mới chạy đối với preauthorize
// @PostAuthorize("returnObject.username == authentication.name")
// public User getUserById(Long id) {
//    User user = userRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("User not found"));
//
//    System.out.println("Return Object Username: " + user.getUsername());
//    System.out.println("Authentication Name: " + SecurityContextHolder.getContext().getAuthentication().getName());
//
//    return user;
// }
