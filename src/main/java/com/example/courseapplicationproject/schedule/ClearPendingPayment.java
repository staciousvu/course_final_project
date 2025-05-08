package com.example.courseapplicationproject.schedule;

import com.example.courseapplicationproject.entity.Payment;
import com.example.courseapplicationproject.repository.PaymentRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClearPendingPayment {
    PaymentRepository paymentRepository;
    @Scheduled(fixedRate = 10*1000*60)
    public void cancelExpiredPendingPayments() {
        List<Payment> expiredPayments = paymentRepository
                .findAllByPaymentStatusAndExpiredTimeBefore(Payment.PaymentStatus.PENDING, LocalDateTime.now());

        expiredPayments.forEach(payment -> {
            payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
            paymentRepository.save(payment);
            log.info("Payment expired and set to FAILED: " + payment.getTransactionId());
        });
    }

}
