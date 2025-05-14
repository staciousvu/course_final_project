package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.event.NotificationEvent;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class TestController {
    KafkaTemplate<String,Object> kafkaTemplate;
    @PostMapping("/message")
    public ResponseEntity<Void> message() {
        Map<String, Object> params = new HashMap<>();
        params.put("username", "John Doe");
        params.put("courseName", "Spring Boot Masterclass");

        NotificationEvent notificationEvent = NotificationEvent.builder()
                .channel("EMAIL")
                .recipient("johndoe@example.com")
                .templateCode("WELCOME_TEMPLATE")
                .subject("Welcome to Spring Boot Course")
                .param(params)
                .build();
        kafkaTemplate.send("test", notificationEvent);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/check")
    public ApiResponse<String> getMessagess() {
        return ApiResponse.success("OKEEEE","OK");
    }
}
