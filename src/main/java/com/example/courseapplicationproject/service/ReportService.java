package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.event.NotificationEmailTemplateData;
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
import jakarta.mail.MessagingException;
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
    MailService mailService;
    ReportRepository reportRepository;
    UserRepository userRepository;
    CourseRepository courseRepository;
    CloudinaryService cloudinaryService;
    public CourseReportResponse getReport(Long reportId){
        CourseReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        return toCourseReportResponse(report);
    }
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
                .reportType(CourseReport.ReportType.valueOf(reportRequest.getReportType()))
                .reason(reportRequest.getReason())
                .description(reportRequest.getDescription())
                .imageUrl(imageUrl)
                .status(CourseReport.ReportStatus.PENDING)
                .build();

        return toCourseReportResponse(reportRepository.save(report));
    }

    public CourseReportResponse approveReport(Long reportId) throws MessagingException, MessagingException {
        CourseReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));

        report.setStatus(CourseReport.ReportStatus.RESOLVED);
        CourseReport savedReport = reportRepository.save(report);

        // Lấy thông tin người báo cáo
        User reporter = report.getUser(); // giả sử report có trường user
        String email = reporter.getEmail();
        String courseName = report.getCourse().getTitle(); // giả sử có liên kết tới khóa học

        // Tạo dữ liệu email
        NotificationEmailTemplateData emailData = NotificationEmailTemplateData.builder()
                .messageTitle("Báo cáo của bạn đã được xử lý")
                .messageBody("Chào " + email + ", báo cáo của bạn liên quan đến khóa học \"" + courseName + "\" đã được xử lý. Cảm ơn bạn đã góp phần cải thiện chất lượng nội dung trên nền tảng.")
                .actionLabel("Xem khóa học")
                .courseImage(report.getCourse().getThumbnail())
                .actionUrl("https://yourdomain.com/courses/" + report.getCourse().getId()) // Tùy chỉnh đường dẫn
                .companyName("Eduflow Platform")
                .recipient(email)
                .build();

        // Gửi email
        mailService.notification(emailData);

        return toCourseReportResponse(savedReport);
    }


    public CourseReportResponse rejectReport(Long reportId) throws MessagingException {
        CourseReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));

        report.setStatus(CourseReport.ReportStatus.REJECTED);
        CourseReport savedReport = reportRepository.save(report);

        // Lấy thông tin người báo cáo
        User reporter = report.getUser(); // giả sử report có trường user
        String email = reporter.getEmail();
        String courseName = report.getCourse().getTitle(); // giả sử report liên kết tới khóa học

        // Tạo nội dung email
        NotificationEmailTemplateData emailData = NotificationEmailTemplateData.builder()
                .messageTitle("Báo cáo của bạn đã bị từ chối")
                .messageBody("Chào " + email + ", sau khi xem xét, báo cáo của bạn về khóa học \"" + courseName + "\" đã bị từ chối. Nếu bạn có thêm thông tin, vui lòng liên hệ bộ phận hỗ trợ.")
                .actionLabel("Liên hệ hỗ trợ")
                .courseImage(report.getCourse().getThumbnail())
                .actionUrl("https://yourdomain.com/support") // Đường dẫn hỗ trợ thực tế
                .companyName("Eduflow Platform")
                .recipient(email)
                .build();

        // Gửi email
        mailService.notification(emailData);

        return toCourseReportResponse(savedReport);
    }

    public List<CourseReportResponse> getPendingReports() {
        return reportRepository.findByStatusOrderByCreatedAtDesc(CourseReport.ReportStatus.PENDING)
                .stream().map(this::toCourseReportResponse).toList();
    }
    public List<CourseReportResponse> getResolvedReports() {
        return reportRepository.findByStatusOrderByCreatedAtDesc(CourseReport.ReportStatus.RESOLVED)
                .stream().map(this::toCourseReportResponse).toList();
    }
    public List<CourseReportResponse> getRejectReports() {
        return reportRepository.findByStatusOrderByCreatedAtDesc(CourseReport.ReportStatus.REJECTED)
                .stream().map(this::toCourseReportResponse).toList();
    }
    private CourseReportResponse toCourseReportResponse(CourseReport report) {
        User user = report.getUser();
        Course course = report.getCourse();

        return CourseReportResponse.builder()
                .id(report.getId())
                .userEmail(user.getEmail())
                .userAvatar(user.getAvatar())
                .reportType(report.getReportType().name())
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
