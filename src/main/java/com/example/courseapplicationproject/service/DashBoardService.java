package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.projection.PerformanceOverviewProjection;
import com.example.courseapplicationproject.dto.projection.StudentEnrollmentProjection;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.EnrollRepository;
import com.example.courseapplicationproject.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class DashBoardService {
    EnrollRepository enrollRepository;
    UserRepository userRepository;
    public Page<StudentEnrollmentProjection> getStudents(Long courseId, String search, String status, int page, int size) {
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Pageable pageable = PageRequest.of(page, size);
        if (courseId != null) {
            return enrollRepository.findStudentEnrollmentsByCourseId(courseId, search, status, pageable);
        } else {
            return enrollRepository.findStudentEnrollmentsByInstructorId(user.getId(), search, status, pageable);
        }
    }
    public List<PerformanceOverviewProjection> getTeacherRevenue(int days, int months, Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (months > 0) {
            return enrollRepository.getRevenueByMonth(user.getId(), courseId, months);
        }

        if (days <= 0) days = 7;
        return enrollRepository.getRevenueByDay(user.getId(), courseId, days);
    }


}
