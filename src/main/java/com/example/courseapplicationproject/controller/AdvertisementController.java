package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.RejectApplyRequest;
import com.example.courseapplicationproject.dto.response.AdsApplyResponse;
import com.example.courseapplicationproject.dto.response.AdvertisementResponse;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.CourseResponse;
import com.example.courseapplicationproject.entity.Advertisement;
import com.example.courseapplicationproject.service.AdvertisementService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
    @RequestMapping("/advertisements")
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    @RequiredArgsConstructor
    public class AdvertisementController {
        AdvertisementService advertisementService;
        // 2. Người dùng áp dụng quảng cáo cho khóa học
        @PostMapping("/apply")
        public ApiResponse<Void> applyAdvertisement(@RequestParam Long advertisementId, @RequestParam Long courseId) {
            advertisementService.applyAdvertisement(advertisementId, courseId);
            return ApiResponse.success(null, "Advertisement applied to course successfully");
        }

    // 3. Admin duyệt quảng cáo
    @PostMapping("/approve/{applyId}")
    public ApiResponse<Void> approveApply(@PathVariable Long applyId) {
        advertisementService.approveApply(applyId);
        return ApiResponse.success(null, "Advertisement approved successfully");
    }

    // 4. Admin từ chối quảng cáo
    @PostMapping("/reject/{applyId}")
    public ApiResponse<Void> rejectApply(@PathVariable Long applyId, @RequestBody RejectApplyRequest request) {
        advertisementService.rejectApply(applyId, request.getReason());
        return ApiResponse.success(null, "Advertisement rejected with reason: " + request.getReason());
    }

    // 5. Lấy danh sách quảng cáo đã mua nhưng chưa dùng
    @GetMapping("/unused")
    public ApiResponse<List<AdvertisementResponse>> getUnusedAdvertisements() {
        return ApiResponse.success(advertisementService.getUnusedAdvertisements(), "Fetched unused advertisements");
    }

    // 6. Lấy danh sách quảng cáo đang được sử dụng và chưa hết hạn
    @GetMapping("/active")
    public ApiResponse<List<AdvertisementResponse>> getActiveAdvertisements() {
        return ApiResponse.success(advertisementService.getActiveAdvertisements(), "Fetched active advertisements");
    }

    // 7. Admin - Lấy danh sách các AdsApply đang chờ duyệt
    @GetMapping("/pending-applies")
    public ApiResponse<List<AdsApplyResponse>> getPendingAdsApplies() {
        return ApiResponse.success(advertisementService.getPendingAdsApplies(), "Fetched pending ads applies");
    }
    // 8.
    @GetMapping("/ads-for-learner")
    public ApiResponse<CourseResponse> getCoursesAdvertisement() {
        return ApiResponse.success(advertisementService.getRandomActiveCourseAdvertisement(), "Fetched unused advertisements");
    }
}
