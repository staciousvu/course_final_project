package com.example.courseapplicationproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticDashboardResponse {
    BigDecimal profitThisMonth;
    Long totalCourseAccept;
    Long totalStudentThisMonth;
    Long totalInstructor;
    ChartPie charPie;
    ChartLine chartLine;
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ChartPie {
        BigDecimal profitCourse;
        BigDecimal profitADS;
    }
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ChartLine{
        List<String> labels;
        List<Long> data;
    }

}
