package com.example.courseapplicationproject.entity;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends AbstractEntity<Long> {

    @Column(name = "first_name", nullable = false, length = 50)
    String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    String lastName;

    @Column(name = "date_of_birth")
    LocalDate dateOfBirth;

    @Column(name = "gender")
    Boolean gender;

    @Column(name = "address", length = 255)
    String address;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    String email;

    @Column(name = "country", length = 50)
    String country;

    @Column(name = "avatar", length = 255)
    String avatar;

    @Column(name = "public_id", length = 255)
    String publicId;

    @Column(name = "bio", columnDefinition = "TEXT")
    String bio;

    @Column(name = "is_enabled", nullable = false)
    Boolean isEnabled;

    @Column(name = "is_teacher_approved", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    boolean isTeacherApproved;

    @Column(name = "facebook_url", length = 255)
    String facebookUrl;

    @Column(name = "twitter_url", length = 255)
    String twitterUrl;

    @Column(name = "linkedin_url", length = 255)
    String linkedinUrl;

    @Column(name = "instagram_url", length = 255)
    String instagramUrl;

    @Column(name = "github_url", length = 255)
    String githubUrl;

    @Column(name = "expertise", length = 255)
    String expertise;

    @Column(name = "cv_url", length = 255)
    String cvUrl;

    @Column(name = "year_of_expertise")
    Integer yearOfExpertise;

    @Column(name = "zipcode", length = 10)
    String zipcode;

    @Column(name = "password", nullable = false)
    String password;

    //    @ManyToOne(fetch = FetchType.LAZY)
    @ManyToMany
    //    @JoinColumn(name = "role_id", nullable = false)
    Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    @JsonIgnore
    Set<Course> courses = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    Set<Favorite> favorites = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    Set<Enrollment> enrollments = new HashSet<>();
}
