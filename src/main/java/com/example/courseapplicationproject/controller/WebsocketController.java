package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.ChatMessage;
import com.example.courseapplicationproject.dto.request.MessageRequest;
import com.example.courseapplicationproject.dto.request.NotificationWebsocket;
import com.example.courseapplicationproject.dto.response.MessageResponse;
import com.example.courseapplicationproject.dto.response.NotificationWebsocketResponse;
import com.example.courseapplicationproject.service.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class WebsocketController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/notification.sendNotification")
    public void sendNotification(NotificationWebsocket notificationWebsocket) {
        messagingTemplate.convertAndSend(
                "/topic/notification",
                notificationWebsocket
        );
    }
}
