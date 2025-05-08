package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.ReportRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.CourseReportResponse;
import com.example.courseapplicationproject.entity.CourseReport;
import com.example.courseapplicationproject.service.ReportService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ReportController {
    ReportService reportService;
    @PostMapping
    public ApiResponse<CourseReportResponse> createReport(@ModelAttribute ReportRequest reportRequest) {
        CourseReportResponse report = reportService.createReport(reportRequest);
        return ApiResponse.success(report, "Tạo báo cáo thành công");
    }

    @PutMapping("/{reportId}/approve")
    public ApiResponse<CourseReportResponse> approveReport(@PathVariable Long reportId) {
        CourseReportResponse report = reportService.approveReport(reportId);
        return ApiResponse.success(report, "Phê duyệt báo cáo thành công");
    }

    @PutMapping("/{reportId}/reject")
    public ApiResponse<CourseReportResponse> rejectReport(@PathVariable Long reportId) {
        CourseReportResponse report = reportService.rejectReport(reportId);
        return ApiResponse.success(report, "Từ chối báo cáo thành công");
    }
    @GetMapping("/pending")
    public ApiResponse<List<CourseReportResponse>> getPendingReports() {
        return ApiResponse.success(reportService.getPendingReports(), "Lấy danh sách report thành công");
    }
}
