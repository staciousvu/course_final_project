package com.example.courseapplicationproject.util;

import java.util.UUID;

public class SlugUtils {
    public static String generateSlug(String name) {
        String baseSlug = name.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .trim();

        String uniqueId = UUID.randomUUID().toString().substring(0, 6);

        return baseSlug + "-" + uniqueId;
    }
}
