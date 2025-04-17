package com.example.courseapplicationproject.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.example.courseapplicationproject.repository.EnrollRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.dto.request.PaymentRequest;
import com.example.courseapplicationproject.entity.Payment;
import com.example.courseapplicationproject.entity.PaymentDetails;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.CourseRepository;
import com.example.courseapplicationproject.repository.PaymentRepository;
import com.example.courseapplicationproject.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class PaymentService {
    UserRepository userRepository;
    CourseRepository courseRepository;
    PaymentRepository paymentRepository;
    EnrollRepository enrollRepository;

    public Payment createPayment(PaymentRequest paymentRequest) {
        String transactionId = UUID.randomUUID().toString();
        User user = userRepository
                .findByEmail(paymentRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        paymentRequest.getCourses().forEach(idx -> {
            if (enrollRepository.existsByCourseIdAndUserId(idx, user.getId())) {
                throw new AppException(ErrorCode.COURSE_ALREADY_PURCHASED);
            }
        });
        Payment payment = Payment.builder()
                .paymentMethod(Payment.PaymentMethod.valueOf(paymentRequest.getPaymentMethod()))
                .paymentStatus(Payment.PaymentStatus.PENDING)
                .expiredTime(LocalDateTime.now().plusMinutes(15))
                .totalAmount(paymentRequest.getTotalAmount())
                .transactionId(transactionId)
                .user(user)
                .paymentInformation(paymentRequest.getOrderInfo())
                .build();
        List<PaymentDetails> paymentDetails = courseRepository.findAllById(paymentRequest.getCourses()).stream()
                .map(course -> {
                    return PaymentDetails.builder()
                            .course(course)
                            .price(course.getPrice())
                            .payment(payment)
                            .build();
                })
                .toList();

        payment.setPaymentDetails(paymentDetails);
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Saved Payment: " + savedPayment);
        log.info("User inside Payment: " + savedPayment.getUser());
        return savedPayment;
    }

    public void updatePaymentStatusToSuccess(String transactionId) {
        Payment payment = paymentRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
        paymentRepository.save(payment);
    }

    public void updatePaymentStatusToFailed(String transactionId) {
        Payment payment = paymentRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));

        payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
        paymentRepository.save(payment);
    }
}
