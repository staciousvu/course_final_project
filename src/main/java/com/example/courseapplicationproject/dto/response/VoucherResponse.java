package com.example.courseapplicationproject.dto.response;

import com.example.courseapplicationproject.entity.Voucher;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherResponse {
    Long id;
    String code;
    String description;
    String discountType;
    BigDecimal discountValue;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Boolean isActive;
    public VoucherResponse(Voucher voucher) {
        this.id = voucher.getId();
        this.code = voucher.getCode();
        this.description = voucher.getDescription();
        this.discountType = voucher.getDiscountType().name();
        this.discountValue = voucher.getDiscountValue();
        this.startDate = voucher.getStartDate();
        this.endDate = voucher.getEndDate();
        this.isActive = voucher.getIsActive();
    }
}
