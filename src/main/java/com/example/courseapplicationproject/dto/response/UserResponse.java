package com.example.courseapplicationproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String firstName;
    String lastName;
    String fullName;
    LocalDate dateOfBirth;
    String favoriteCategory;
    Boolean gender;
    String address;
    String email;
    String country;
    String avatar;
    LocalDateTime createdAt;
    String bio;
    Boolean isEnabled;
    Boolean isTeacherApproved;
    String facebookUrl;
    String twitterUrl;
    String linkedinUrl;
    String instagramUrl;
    String githubUrl;
    String expertise;
    String cvUrl;
    Integer yearOfExpertise;
    String zipcode;
}
