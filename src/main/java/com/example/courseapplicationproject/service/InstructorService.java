package com.example.courseapplicationproject.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.dto.response.InfoInstructorByCourseResponse;
import com.example.courseapplicationproject.entity.Course;
import com.example.courseapplicationproject.entity.Role;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.CourseRepository;
import com.example.courseapplicationproject.repository.CourseReviewRepository;
import com.example.courseapplicationproject.repository.RoleRepository;
import com.example.courseapplicationproject.repository.UserRepository;
import com.example.courseapplicationproject.service.interfaces.IInstructorService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class InstructorService implements IInstructorService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    CourseRepository courseRepository;
    CourseReviewRepository courseReviewRepository;

    @Override
    public void becomeInstructor() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (!user.getIsTeacherApproved()) {
            Role role = roleRepository
                    .findByRoleName(Role.RoleType.INSTRUCTOR.toString())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            user.getRoles().add(role);
            userRepository.save(user);
        }
    }

    @Override
    public InfoInstructorByCourseResponse getInfoInstructorByCourse(Long courseId) {
        Course course =
                courseRepository.findById(courseId).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        User user = course.getAuthor();
        String fullName = user.getFirstName() + " " + user.getLastName();
        return InfoInstructorByCourseResponse.builder()
                .id(user.getId())
                .bio(user.getBio())
                .avatar(user.getAvatar())
                .fullName(fullName)
                .reviewCount(courseReviewRepository.countReviewsByCourse(courseId))
                .avgRating(courseReviewRepository.avgRatingByCourse(courseId))
                .studentCount(userRepository.countStudentsByTeacherId(user.getId()))
                .courseCount(courseRepository.countCourseByByAuthor(user.getId()))
                .build();
    }
}
