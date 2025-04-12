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
public class AdminCreateDTO {
    private String firstName;
    private String lastName;
    private String email;
    private Boolean gender;
    private LocalDate birthDate;
    private String password;
    private String avatar;
    private Boolean isEnabled;
}
