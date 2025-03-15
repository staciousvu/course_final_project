package com.example.courseapplicationproject.dto.response;

import java.math.BigDecimal;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InfoInstructorByCourseResponse {
    Long id;
    String fullName;
    String avatar;
    String bio;
    BigDecimal avgRating;
    int reviewCount;
    int studentCount;
    int courseCount;
}
