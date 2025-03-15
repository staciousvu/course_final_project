package com.example.courseapplicationproject.dto.request;

import java.time.LocalDate;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Boolean gender;
    private String address;
    private String country;
    private String bio;
    private String facebookUrl;
    private String twitterUrl;
    private String linkedinUrl;
    private String instagramUrl;
    private String githubUrl;
    private String expertise;
    private Integer yearOfExpertise;
}
