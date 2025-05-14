package com.example.courseapplicationproject.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentRequest {
    String email;

    @JsonProperty("payment_method")
    String paymentMethod;

    String orderType; // "COURSE" hoặc "AD"

    @JsonProperty("total_amount")
    BigDecimal totalAmount;

    @JsonProperty("courses")
    List<Long> courses;

    @JsonProperty("order_info")
    String orderInfo;

    Long adPackageId;
}
