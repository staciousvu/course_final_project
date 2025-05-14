package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.request.ReportRequest;
import com.example.courseapplicationproject.dto.response.CourseReportResponse;
import com.example.courseapplicationproject.entity.Course;
import com.example.courseapplicationproject.entity.CourseReport;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.CourseRepository;
import com.example.courseapplicationproject.repository.ReportRepository;
import com.example.courseapplicationproject.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class ReportService {
    ReportRepository reportRepository;
    UserRepository userRepository;
    CourseRepository courseRepository;
    CloudinaryService cloudinaryService;
    public CourseReportResponse createReport(ReportRequest reportRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Course course = courseRepository.findById(reportRequest.getCourseId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Map result = cloudinaryService.uploadImage(reportRequest.getImage());
        String imageUrl = result.get("secure_url").toString();

        CourseReport report = CourseReport.builder()
                .user(user)
                .course(course)
                .reason(reportRequest.getReason())
                .description(reportRequest.getDescription())
                .imageUrl(imageUrl)
                .status(CourseReport.ReportStatus.PENDING)
                .build();

        return toCourseReportResponse(reportRepository.save(report));
    }

    public CourseReportResponse approveReport(Long reportId) {
        CourseReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));

        report.setStatus(CourseReport.ReportStatus.RESOLVED);
        return toCourseReportResponse(reportRepository.save(report));
    }

    public CourseReportResponse rejectReport(Long reportId) {
        CourseReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));

        report.setStatus(CourseReport.ReportStatus.REJECTED);
        return toCourseReportResponse(reportRepository.save(report));
    }
    public List<CourseReportResponse> getPendingReports() {
        return reportRepository.findByStatusOrderByCreatedAtDesc(CourseReport.ReportStatus.PENDING)
                .stream().map(this::toCourseReportResponse).toList();
    }
    private CourseReportResponse toCourseReportResponse(CourseReport report) {
        User user = report.getUser();
        Course course = report.getCourse();

        return CourseReportResponse.builder()
                .id(report.getId())
                .userEmail(user.getEmail())
                .userAvatar(user.getAvatar())
                .userFullName(user.getLastName() + " " + user.getFirstName())
                .courseTitle(course.getTitle())
                .courseThumbnail(course.getThumbnail())
                .courseId(course.getId())
                .authorFullName(course.getAuthor().getFirstName() + " " + course.getAuthor().getLastName())
                .reason(report.getReason())
                .description(report.getDescription())
                .imageUrl(report.getImageUrl())
                .status(report.getStatus().name())
                .createdAt(report.getCreatedAt())
                .build();
    }


}
