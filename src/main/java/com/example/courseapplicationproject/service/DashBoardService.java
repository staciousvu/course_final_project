package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.projection.PerformanceOverviewProjection;
import com.example.courseapplicationproject.dto.projection.StudentEnrollmentProjection;
import com.example.courseapplicationproject.dto.response.OverviewInstructorResponse;
import com.example.courseapplicationproject.dto.response.StatisticDashboardResponse;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public OverviewInstructorResponse getOverviewInstructor(Long courseId){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return OverviewInstructorResponse.builder()
                .totalEnrollments(enrollRepository.countTotalEnrollmentsByTeacher(user.getId(),courseId))
                .totalEnrollmentsThisMonth(enrollRepository.countMonthlyEnrollmentsByTeacher(user.getId(),courseId))
                .totalRevenue(enrollRepository.getTotalRevenue(user.getId(),courseId))
                .totalRevenueThisMonth(enrollRepository.getTotalRevenueThisMonth(user.getId(),courseId))
                .build();
    }
    public StatisticDashboardResponse getStatisticDashboard() {
        // Lấy ngày bắt đầu từ 6 tháng trước
        LocalDateTime startDate = LocalDate.now().withDayOfMonth(1).minusMonths(5).atStartOfDay();

        // Lấy dữ liệu từ repository
        List<Object[]> enrollments = enrollRepository.countNewEnrollmentsEachMonth(startDate);

        // Tạo map từ tháng (1-12) sang số lượng học viên
        Map<Integer, Long> monthToCountMap = enrollments.stream()
                .collect(Collectors.toMap(
                        row -> (Integer) row[0],
                        row -> (Long) row[1]
                ));

        // Tạo danh sách label và data theo thứ tự thời gian
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            LocalDate date = LocalDate.now().withDayOfMonth(1).minusMonths(5 - i);
            int month = date.getMonthValue();

            labels.add(date.format(formatter));
            data.add(monthToCountMap.getOrDefault(month, 0L));
        }

        // Trả về response
        return StatisticDashboardResponse.builder()
                .profitThisMonth(enrollRepository.getProfitAdsAndCourseThisMonth())
                .charPie(StatisticDashboardResponse.ChartPie.builder()
                        .profitADS(enrollRepository.getAdProfit())
                        .profitCourse(enrollRepository.getCourseProfit())
                        .build())
                .totalCourseAccept(enrollRepository.countAcceptedCourses())
                .totalInstructor(enrollRepository.countUsersWithAcceptedCourses())
                .totalStudentThisMonth(enrollRepository.countUniqueUsersEnrolledThisMonth())
                .chartLine(StatisticDashboardResponse.ChartLine.builder()
                        .labels(labels)
                        .data(data)
                        .build())
                .build();
    }




}
