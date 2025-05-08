package com.example.courseapplicationproject.controller;

import java.io.IOException;

import com.example.courseapplicationproject.dto.response.PaymentResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.example.courseapplicationproject.dto.request.PaymentRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.entity.Payment;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.PaymentRepository;
import com.example.courseapplicationproject.service.CourseService;
import com.example.courseapplicationproject.service.PaymentService;
import com.example.courseapplicationproject.service.VNPayService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PaymentController {
    PaymentRepository paymentRepository;
    PaymentService paymentService;
    VNPayService vnPayService;
    CourseService courseService;

    @PostMapping("/vn-pay")
    public ApiResponse<String> pay(@RequestBody PaymentRequest paymentRequest, HttpServletRequest request) {
        Payment payment = paymentService.createPayment(paymentRequest);
        return ApiResponse.success(vnPayService.createVNPayUrl(payment, request), "Create vnpayUrl successfully");
    }

    @GetMapping("/callback")
    public void callback(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String transactionId = request.getParameter("vnp_TxnRef");
        String transactionStatus = request.getParameter("vnp_TransactionStatus");
        Payment payment = paymentRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.PAYMENT_NOT_FOUND));
        if ("00".equals(transactionStatus)) {
            courseService.enrollCourse(payment);
            paymentService.updatePaymentStatusToSuccess(transactionId);

            response.sendRedirect("http://localhost:4200/payment-success");
        } else {
            paymentService.updatePaymentStatusToFailed(transactionId);
            response.sendRedirect("http://localhost:4200/payment-failed");
        }
    }
    @GetMapping
    public ApiResponse<Page<PaymentResponseDTO>> getPayments(
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<PaymentResponseDTO> result = paymentService.getPayments(email, page, size);
        return ApiResponse.success(result, "Payments fetched successfully");
    }
}
