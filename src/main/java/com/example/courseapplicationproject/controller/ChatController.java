package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.ChatMessage;
import com.example.courseapplicationproject.dto.request.MessageRequest;
import com.example.courseapplicationproject.dto.response.MessageResponse;
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
public class ChatController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage chatMessage) {
        MessageRequest request = new MessageRequest();
        request.setConversationId(chatMessage.getConversationId());
        request.setSenderId(chatMessage.getSenderId());
        request.setContent(chatMessage.getContent());

        MessageResponse savedMessage = messageService.sendMessage(request);

        messagingTemplate.convertAndSend(
                "/topic/conversations/" + chatMessage.getConversationId(),
                savedMessage
        );
    }
}
