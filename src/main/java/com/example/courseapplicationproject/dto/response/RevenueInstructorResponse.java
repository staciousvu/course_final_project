package com.example.courseapplicationproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RevenueInstructorResponse {
    BigDecimal revenue;
    Integer totalStudents;
    Integer totalCourses;
    @Builder.Default
    List<RevenueCourseInstructor> revenueCourseInstructors = new ArrayList<>();
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class RevenueCourseInstructor {
        Long id;
        String courseName;
        BigDecimal price;
        Integer enrolledStudents;
        BigDecimal revenue;
    }
}
