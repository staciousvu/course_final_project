package com.example.courseapplicationproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OverviewInstructorResponse {
    BigDecimal totalRevenue;
    BigDecimal totalRevenueThisMonth;
    Integer totalEnrollments;
    Integer totalEnrollmentsThisMonth;
}
