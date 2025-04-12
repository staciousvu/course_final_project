package com.example.courseapplicationproject.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherRequest implements Serializable {
    String code;
    String description;
    String discountType;
    BigDecimal discountValue;
    LocalDateTime startDate;
    LocalDateTime endDate;
    Boolean isActive;
}
