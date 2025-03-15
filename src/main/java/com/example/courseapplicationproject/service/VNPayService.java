package com.example.courseapplicationproject.service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.entity.Payment;
import com.example.courseapplicationproject.util.VNPayUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VNPayService {
    @Value("${vnp_Url}")
    private String vnp_PayUrl;

    @Value("${vnp_Returnurl}")
    private String vnp_Returnurl;

    @Value("${vnp_TmnCode}")
    private String vnp_TmnCode;

    @Value("${vnp_HashSecret}")
    private String vnp_HashSecret;

    public String createVNPayUrl(Payment payment, HttpServletRequest request) {

        Map<String, String> vnParams = new HashMap<>();
        vnParams.put("vnp_Version", "2.1.0");
        vnParams.put("vnp_Command", "pay");
        vnParams.put("vnp_TmnCode", vnp_TmnCode);
        vnParams.put("vnp_CurrCode", "VND");
        vnParams.put("vnp_OrderType", "payment-bill");
        vnParams.put("vnp_Locale", "vn");
        vnParams.put(
                "vnp_ReturnUrl",
                request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + vnp_Returnurl);
        vnParams.put("vnp_IpAddr", VNPayUtils.getIpAddress(request));
        vnParams.put("vnp_Amount", String.valueOf(payment.getTotalAmount().multiply(new BigDecimal(100))));
        vnParams.put("vnp_OrderInfo", payment.getPaymentInformation());
        vnParams.put("vnp_TxnRef", payment.getTransactionId());

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnParams.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnParams.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnParams.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<?> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnParams.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayUtils.hmacSHA512(vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        return vnp_PayUrl + "?" + queryUrl;
    }
}
