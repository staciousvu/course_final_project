package com.example.courseapplicationproject.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.example.courseapplicationproject.dto.response.PaymentResponseDTO;
import com.example.courseapplicationproject.repository.EnrollRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    VoucherService voucherService;
    public Page<PaymentResponseDTO> getPayments(String email,Integer page,Integer size) {
        Page<Payment> payments;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (email != null && !email.trim().isEmpty()) {
            payments = paymentRepository.findByUserEmailContainingIgnoreCase(email, pageRequest);
        } else {
            payments = paymentRepository.findAll(pageRequest);
        }

        return payments.map(payment -> PaymentResponseDTO.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .email(payment.getUser().getEmail())
                .fullName(payment.getUser().getFirstName()+" "+payment.getUser().getLastName())
                .avatar(payment.getUser().getAvatar())
                .paymentMethod(payment.getPaymentMethod().name())
                .paymentStatus(payment.getPaymentStatus().name())
                .expiredTime(payment.getExpiredTime())
                .totalAmount(payment.getTotalAmount())
                .paymentInformation(payment.getPaymentInformation())
                .paymentDetails(payment.getPaymentDetails().stream().map(detail ->
                        PaymentResponseDTO.Detail.builder()
                                .courseId(detail.getCourse().getId())
                                .courseName(detail.getCourse().getTitle())
                                .urlImage(detail.getCourse().getThumbnail())
                                .price(detail.getPrice())
                                .build()
                ).toList())
                .build());
    }


    public Payment createPayment(PaymentRequest paymentRequest) {
        String transactionId = UUID.randomUUID().toString();

        User user = userRepository
                .findByEmail(paymentRequest.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Payment.OrderType orderType = Payment.OrderType.valueOf(paymentRequest.getOrderType());

        // Kiểm tra nếu là mua khóa học
        if (orderType == Payment.OrderType.COURSE) {
            // Check người dùng đã mua khóa học chưa
            paymentRequest.getCourses().forEach(courseId -> {
                if (enrollRepository.existsByCourseIdAndUserId(courseId, user.getId())) {
                    throw new AppException(ErrorCode.COURSE_ALREADY_PURCHASED);
                }
            });
        }

        // Khởi tạo payment
        Payment.PaymentBuilder builder = Payment.builder()
                .paymentMethod(Payment.PaymentMethod.valueOf(paymentRequest.getPaymentMethod()))
                .paymentStatus(Payment.PaymentStatus.PENDING)
                .expiredTime(LocalDateTime.now().plusMinutes(15))
                .totalAmount(paymentRequest.getTotalAmount())
                .orderType(orderType)
                .transactionId(transactionId)
                .user(user)
                .paymentInformation(paymentRequest.getOrderInfo());

        // Nếu là quảng cáo thì thêm adPackageId
        if (orderType == Payment.OrderType.AD) {
            builder.adPackageId(paymentRequest.getAdPackageId());
        }

        Payment payment = builder.build();

        // Nếu là mua khóa học thì thêm paymentDetails
        if (orderType == Payment.OrderType.COURSE) {
            List<PaymentDetails> paymentDetails = courseRepository.findAllById(paymentRequest.getCourses())
                    .stream()
                    .map(course -> PaymentDetails.builder()
                            .course(course)
                            .price(voucherService.calculateDiscountedPrice(course.getPrice()))
                            .payment(payment)
                            .build())
                    .toList();
            payment.setPaymentDetails(paymentDetails);
        }

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Saved Payment: {}", savedPayment);
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
