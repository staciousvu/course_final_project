package com.example.courseapplicationproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "ads_apply")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdsApply extends AbstractEntity<Long>{
    // Gói quảng cáo đã mua và muốn áp dụng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advertisement_id", nullable = false)
    Advertisement advertisement;

    // Khóa học muốn quảng cáo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    Course course;

    // Trạng thái: PENDING, APPROVED, REJECTED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ApplicationStatus status;

    // Lý do từ chối nếu có
    @Column(columnDefinition = "TEXT")
    String rejectionReason;


    public enum ApplicationStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
