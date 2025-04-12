package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.VoucherRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.VoucherResponse;
import com.example.courseapplicationproject.service.VoucherService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voucher")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class VoucherController {
    VoucherService voucherService;
    @PostMapping("/create")
    public ApiResponse<Void> createVoucher(@RequestBody VoucherRequest voucherRequest) {
        voucherService.createVoucher(voucherRequest);
        return ApiResponse.success(null,"Create voucher successfully");
    }
    @PostMapping("/active-voucher/{id}")
    public ApiResponse<Void> activateVoucher(@PathVariable Long id) {
        voucherService.activateVoucher(id);
        return ApiResponse.success(null,"Activate voucher successfully");
    }
    @PostMapping("/toggle-active-voucher/{id}")
    public ApiResponse<Void> toggleActivateVoucher(@PathVariable Long id) {
        voucherService.toggleActiveVoucher(id);
        return ApiResponse.success(null,"Toggle activate voucher successfully");
    }
    @PostMapping("/update-latest")
    public ApiResponse<Void> updateLatest() {
        voucherService.updateExpiredVouchers();
        return ApiResponse.success(null,"Toggle activate voucher successfully");
    }
    @PostMapping("/deactivate-voucher/{id}")
    public ApiResponse<Void> deactivateVoucher(@PathVariable Long id) {
        voucherService.deactivateVoucher(id);
        return ApiResponse.success(null,"Activate voucher successfully");
    }
    @GetMapping("/getActiveVoucherNonExpired")
    public ApiResponse<List<VoucherResponse>> getActiveVoucherNonExpired() {
        return ApiResponse.success(voucherService.getActiveVoucherNonExpired(),"Get active voucher successfully");
    }
    @GetMapping("/getInactiveVoucherNonExpired")
    public ApiResponse<List<VoucherResponse>> getInactiveVoucherNonExpired() {
        return ApiResponse.success(voucherService.getInactiveVoucherNonExpired(),"Get active voucher successfully");
    }
    @GetMapping("/getInactiveVoucherExpired")
    public ApiResponse<List<VoucherResponse>> getInactiveVoucherExpired() {
        return ApiResponse.success(voucherService.getInactiveVoucherExpired(),"Get active voucher successfully");
    }
    @GetMapping("/getInactiveNotStarted")
    public ApiResponse<List<VoucherResponse>> getInactiveNotStarted() {
        return ApiResponse.success(voucherService.getInactiveNotStarted(),"Get active voucher successfully");
    }
}
