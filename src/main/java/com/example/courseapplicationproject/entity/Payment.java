package com.example.courseapplicationproject.entity;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "payment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment extends AbstractEntity<Long> {
    public enum PaymentMethod {
        VNPAY,
        MOMO,
        ZALOPAY,
        VNPAYQR
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;

    public enum PaymentStatus {
        PENDING,
        FAILED,
        SUCCESS,
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    PaymentStatus paymentStatus;

    @Column(name = "transaction_id", unique = true)
    String transactionId;

    BigDecimal totalAmount;

    String paymentInformation;

    @OneToMany(mappedBy = "payment", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<PaymentDetails> paymentDetails;
}
