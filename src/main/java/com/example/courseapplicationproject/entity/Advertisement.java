package com.example.courseapplicationproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "advertisement")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Advertisement extends AbstractEntity<Long>{

    // Người dùng mua quảng cáo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    // Gói quảng cáo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_package_id", nullable = false)
    AdPackage adPackage;

    boolean used = false;

    LocalDateTime startDate;

    LocalDateTime endDate;


}
