// package com.example.courseapplicationproject.configuration;
//
// import java.util.*;
//
// import jakarta.servlet.http.HttpServletRequest;
//
// import org.springframework.beans.factory.annotation.Value;
//
// import com.example.courseapplicationproject.entity.Payment;
// import com.example.courseapplicationproject.util.VNPayUtils;
// import org.springframework.stereotype.Component;
//
// @Component
// public class VNPayConfig {
//    @Value("${vnp_Url}")
//    private String vnp_PayUrl;
//
//    @Value("${vnp_Returnurl}")
//    private String vnp_Returnurl;
//
//    @Value("${vnp_TmnCode}")
//    private String vnp_TmnCode;
//
//    @Value("${vnp_HashSecret}")
//    private String vnp_HashSecret;
//
//    public Map<String, String> getVNPayParams(Payment payment, HttpServletRequest request) {
//        Map<String, String> vnParams = new HashMap<>();
//        vnParams.put("vnp_Version", "2.1.0");
//        vnParams.put("vnp_Command", "pay");
//        vnParams.put("vnp_TmnCode", vnp_TmnCode);
//        vnParams.put("vnp_CurrCode", "VND");
//        vnParams.put("vnp_OrderType", "payment-bill");
//        vnParams.put("vnp_Locale", "vn");
//        vnParams.put(
//                "vnp_ReturnUrl",
//                request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() +
// vnp_Returnurl);
//        vnParams.put("vnp_HashSecret", vnp_HashSecret);
//        vnParams.put("vnp_IpAddr", VNPayUtils.getIpAddress(request));
//        vnParams.put("vnp_Amount", payment.getTotalAmount().toString());
//        vnParams.put("vnp_OrderInfo", payment.getPaymentInformation());
//        vnParams.put("vnp_TxnRef", payment.getTransactionId());
//        vnParams.put("vnp_PayUrl", payment.getTransactionId());
//        return vnParams;
//    }
// }
//
