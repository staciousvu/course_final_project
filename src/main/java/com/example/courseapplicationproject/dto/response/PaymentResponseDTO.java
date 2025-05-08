package com.example.courseapplicationproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponseDTO {
    Long id;
    String transactionId;
    String email;
    String fullName;
    String avatar;
    String paymentMethod;
    String paymentStatus;
    LocalDateTime expiredTime;
    BigDecimal totalAmount;
    String paymentInformation;

    List<Detail> paymentDetails;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Detail {
        Long courseId;
        String courseName;
        String urlImage;
        BigDecimal price;
    }
}
