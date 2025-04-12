package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.response.RevenueInstructorResponse;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
@Slf4j
public class RevenueService {
    UserRepository userRepository;
    EnrollRepository enrollRepository;
    CourseRepository courseRepository;
    PaymentRepository paymentRepository;
    CourseReviewRepository courseReviewRepository;
    public RevenueInstructorResponse getRevenueInstructor(Long instructorId) {
        User user = userRepository.findById(instructorId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        BigDecimal revenue = courseRepository.revenueForTeacher(instructorId);
        Integer totalCourses = courseRepository.countCourseByByAuthor(instructorId);
        Integer totalStudents = enrollRepository.countTotalStudents(instructorId);

        List<RevenueInstructorResponse.RevenueCourseInstructor> revenueCourseInstructors = courseRepository.findCourseForAuthor(instructorId)
                .stream()
                .map(course -> {
                    Integer enrolledStudents = enrollRepository.countEnrolledForCourse(course.getId());
                    BigDecimal courseRevenue = paymentRepository.revenueByCourse(course.getId());
                    return RevenueInstructorResponse.RevenueCourseInstructor.builder()
                            .id(course.getId())
                            .courseName(course.getTitle())
                            .price(course.getPrice())
                            .enrolledStudents(enrolledStudents)
                            .revenue(courseRevenue)
                            .build();
                })
                .collect(Collectors.toList());

        return RevenueInstructorResponse.builder()
                .revenue(revenue != null ? revenue : BigDecimal.ZERO)
                .totalStudents(totalStudents != null ? totalStudents : 0)
                .totalCourses(totalCourses != null ? totalCourses : 0)
                .revenueCourseInstructors(revenueCourseInstructors)
                .build();
    }
}
