package com.example.courseapplicationproject.util;

import com.example.courseapplicationproject.entity.User;

public class UserUtils {
    public static boolean isAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equalsIgnoreCase("ADMIN"));
    }
}
