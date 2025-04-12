package com.example.courseapplicationproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstructorListResponse {
    private Long id;
    private String name;
    private String email;
    private Boolean gender;
    private LocalDate birthDate;
    private Integer totalCourses;
    private Integer totalStudents;
    private String avatar;
    private Boolean isEnabled;
}
