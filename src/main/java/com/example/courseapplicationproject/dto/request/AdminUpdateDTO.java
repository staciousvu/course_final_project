package com.example.courseapplicationproject.dto.request;

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
public class AdminUpdateDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String password;
    private Boolean gender;
    private String avatar;
    private Boolean isEnable;
}
