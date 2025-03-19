//package com.example.courseapplicationproject.entity;
//
//import jakarta.persistence.FetchType;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//
//public class UserActivity {
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    @JsonIgnore
//    User user;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "course_id", nullable = false)
//    @JsonIgnore
//    Course course;
//}
