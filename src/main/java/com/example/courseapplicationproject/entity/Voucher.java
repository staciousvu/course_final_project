package com.example.courseapplicationproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Voucher extends AbstractEntity<Long>{
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 38, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "is_active")
//    private Status isActive = Status.ACTIVE;

    @Column(name = "is_active")
    private Boolean isActive;

    // Enum cho discount_type
    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT
    }

    // Enum cho is_active
//    public enum Status {
//        ACTIVE,
//        INACTIVE
//    }

}
