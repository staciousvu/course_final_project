package com.example.courseapplicationproject.configuration;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    @Value("${jwt.secret-key}")
    protected String secretKey;

    protected final String[] publicEndpoints = {
            "/images/**",
            "/videos/**",
        "/auth/log-in",
        "/auth/introspect",
        "/auth/refresh",
        "/test/message",
        "/user/sent-otp",
        "/user/create",
        "/payment/vn-pay",
        "/payment/callback",
        "/user/sent-otp-reset",
        "/user/verify-otp-reset",
        "/user/reset-password",
        "/categories/**",
        "/categories/parent/**",
        "/course/search/**",
            "/voucher/create",
            "/voucher/active-voucher",
            "/voucher/inactive-expired-vouchers",
            "/voucher/active-voucher/**",
            "/voucher/deactivate-voucher/**",
            "/course/*/sections-lectures/no-auth",
            "/course/course-detail/**",
            "recommend/root",
            "recommend/leafs",
            "recommend/activity",
            "recommend/related-enrolled",
            "recommend/category/**",
            "recommend/recommend-admin",
            "/ws/**"
        //            "/recommend/root",
        //            "/recommend/leafs"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(publicEndpoints)
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/hello")
                        .hasAuthority("INSTRUCTOR")
                        .requestMatchers(HttpMethod.GET, "/auth/learn")
                        .hasAuthority("LEARNER")
                        .requestMatchers(HttpMethod.POST, "/permissions/create")
                        .hasAuthority("ADMIN")
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(exp -> exp.accessDeniedHandler(new AccessDeniedHandlerSecurity()))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new AuthenticationEntryPointSecurity()));
        http.csrf(AbstractHttpConfigurer::disable);
        http.headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable) // Tạm thời vô hiệu hóa X-Frame-Options
        );
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HS256");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("scope");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
