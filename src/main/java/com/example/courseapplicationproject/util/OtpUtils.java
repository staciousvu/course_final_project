package com.example.courseapplicationproject.util;

import java.util.Random;
import java.util.StringJoiner;

public class OtpUtils {
    private static Random random = new Random();

    public static String generateOtp() {
        StringJoiner joiner = new StringJoiner("");
        for (int i = 0; i < 6; i++) joiner.add(String.valueOf(random.nextInt(9)));
        return joiner.toString();
    }
}
