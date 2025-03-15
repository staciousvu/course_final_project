package com.example.courseapplicationproject.util;

import jakarta.servlet.http.HttpServletRequest;

public class JwtUtils {
    public static String getToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
