package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.ConversationRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.ConversationResponse;
import com.example.courseapplicationproject.entity.Conversation;
import com.example.courseapplicationproject.service.ConversationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversations")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class ConversationController {
    ConversationService conversationService;
    @GetMapping("/get-or-create")
    public ApiResponse<ConversationResponse> getOrCreateConversation(
            @RequestParam Long instructorId
    ) {
        return ApiResponse.success(
                conversationService.getOrCreateConversation(instructorId),
                "OK"
        );
    }

    @GetMapping("/student")
    public ApiResponse<List<ConversationResponse>> getConversationsForStudent(
            @RequestParam String email
    ) {
        return ApiResponse.success(
                conversationService.getConversationsForStudent(email),
                "OK"
        );
    }

    @GetMapping("/instructor")
    public ApiResponse<List<ConversationResponse>> getConversationsForInstructor(
            @RequestParam String email
    ) {
        return ApiResponse.success(
                conversationService.getConversationsForInstructor(email),
                "OK"
        );
    }
}
