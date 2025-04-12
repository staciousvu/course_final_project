package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.request.VoucherRequest;
import com.example.courseapplicationproject.dto.response.VoucherResponse;
import com.example.courseapplicationproject.entity.Voucher;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.VoucherRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class VoucherService {
    VoucherRepository voucherRepository;
    public void createVoucher(VoucherRequest request) {
        if (voucherRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.VOUCHER_EXISTED);
        }
        Voucher voucher = Voucher.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .discountType(Voucher.DiscountType.valueOf(request.getDiscountType().toUpperCase()))
                .discountValue(request.getDiscountValue())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(request.getIsActive())
                .build();
//        return new VoucherResponse(voucherRepository.save(voucher));
        List<Voucher> expiredVouchers = voucherRepository.findByIsActiveTrue()
                .stream()
                .filter(v -> LocalDateTime.now().isBefore(v.getEndDate()))
                .toList();
        if (!expiredVouchers.isEmpty()) {
            expiredVouchers.forEach(v -> v.setIsActive(false));
            voucherRepository.saveAll(expiredVouchers);
        }
        voucherRepository.save(voucher);
    }
//    here
    public List<VoucherResponse> getActiveVoucherNonExpired(){
        return voucherRepository.findByIsActiveTrue()
                .stream()
                .filter(v -> LocalDateTime.now().isAfter(v.getStartDate()) && LocalDateTime.now().isBefore(v.getEndDate()))
                .map(VoucherResponse::new)
                .toList();
    }
    public List<VoucherResponse> getInactiveVoucherNonExpired(){
        return voucherRepository.findByIsActiveFalse()
                .stream()
                .filter(v -> LocalDateTime.now().isAfter(v.getStartDate()) && LocalDateTime.now().isBefore(v.getEndDate()))
                .map(VoucherResponse::new)
                .collect(Collectors.toList());
    }
    public List<VoucherResponse> getInactiveVoucherExpired(){
        return voucherRepository.findByIsActiveFalse()
                .stream()
                .filter(v -> LocalDateTime.now().isAfter(v.getEndDate()))
                .map(VoucherResponse::new)
                .collect(Collectors.toList());
    }
    public List<VoucherResponse> getInactiveNotStarted(){
        return voucherRepository.findByIsActiveFalse()
                .stream()
                .filter(v -> LocalDateTime.now().isBefore(v.getStartDate()))
                .map(VoucherResponse::new)
                .collect(Collectors.toList());
    }
//    here

    public void activateVoucher(Long id) {

        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        if (voucher.getIsActive()) {
            return;
        }

        if (LocalDateTime.now().isBefore(voucher.getStartDate()) || LocalDateTime.now().isAfter(voucher.getEndDate())) {
            throw new AppException(ErrorCode.CAN_NOT_ACTIVATE);
        }

        // Vô hiệu hóa tất cả các voucher đang ACTIVE
        List<Voucher> activeVouchers = voucherRepository.findByIsActiveTrue();
        activeVouchers.forEach(v -> v.setIsActive(false));
        voucherRepository.saveAll(activeVouchers);

        // Kích hoạt voucher mới
        voucher.setIsActive(true);
        voucherRepository.save(voucher);
    }
    public void toggleActiveVoucher(Long id) {

        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));

        if (voucher.getIsActive()) {
            voucher.setIsActive(false);
            voucherRepository.save(voucher);
            return;
        }

        if (LocalDateTime.now().isBefore(voucher.getStartDate()) || LocalDateTime.now().isAfter(voucher.getEndDate())) {
            throw new AppException(ErrorCode.CAN_NOT_ACTIVATE);
        }

        // Vô hiệu hóa tất cả các voucher đang ACTIVE
        List<Voucher> activeVouchers = voucherRepository.findByIsActiveTrue();
        activeVouchers.forEach(v -> v.setIsActive(false));
        voucherRepository.saveAll(activeVouchers);

        // Kích hoạt voucher mới
        voucher.setIsActive(true);
        voucherRepository.save(voucher);
    }

    public void deactivateVoucher(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        voucher.setIsActive(false);
        voucherRepository.save(voucher);
    }

    public boolean isVoucherValid(String code) {
        updateExpiredVouchers(); // Cập nhật trạng thái voucher trước khi kiểm tra
        return voucherRepository.findByCode(code)
                .filter(Voucher::getIsActive)
                .filter(v -> LocalDateTime.now().isAfter(v.getStartDate()) && LocalDateTime.now().isBefore(v.getEndDate()))
                .isPresent();
    }

    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice) {
        Optional<Voucher> voucher = voucherRepository.findByIsActiveTrue()
                .stream()
                .findFirst();
        updateExpiredVouchers(); // Đảm bảo voucher hợp lệ trước khi tính toán
        return voucher
                .filter(v -> LocalDateTime.now().isAfter(v.getStartDate()) && LocalDateTime.now().isBefore(v.getEndDate()))
                .map(v -> {
                    if (v.getDiscountType() == Voucher.DiscountType.PERCENTAGE) {
                        return originalPrice.subtract(originalPrice.multiply(v.getDiscountValue().divide(BigDecimal.valueOf(100))));
                    } else {
                        return originalPrice.subtract(v.getDiscountValue()).max(BigDecimal.ZERO);
                    }
                }).orElse(originalPrice);
    }


    public void updateExpiredVouchers() {
        List<Voucher> expiredVouchers = voucherRepository.findByIsActiveTrue()
                .stream()
                .filter(v -> LocalDateTime.now().isAfter(v.getEndDate()))
                .toList();
        if (!expiredVouchers.isEmpty()) {
            expiredVouchers.forEach(v -> v.setIsActive(false));
            voucherRepository.saveAll(expiredVouchers);
        }
    }
}
