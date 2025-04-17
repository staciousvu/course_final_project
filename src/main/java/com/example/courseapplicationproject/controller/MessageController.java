package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.MessageRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.MessageResponse;
import com.example.courseapplicationproject.service.MessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public ApiResponse<MessageResponse> sendMessage(@RequestBody MessageRequest request) {
        return ApiResponse.success(messageService.sendMessage(request), "OK");
    }

    @GetMapping("/{conversationId}")
    public ApiResponse<List<MessageResponse>> getMessages(@PathVariable Long conversationId) {
        return ApiResponse.success(messageService.getMessages(conversationId), "OK");
    }

    @PutMapping("/read")
    public ApiResponse<String> markMessagesAsRead(
            @RequestParam Long conversationId,
            @RequestParam Long currentUserId
    ) {
        messageService.markMessagesAsRead(conversationId, currentUserId);
        return ApiResponse.success("Marked as read", "OK");
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> countUnreadMessages(
            @RequestParam Long conversationId,
            @RequestParam Long currentUserId
    ) {
        return ApiResponse.success(
                messageService.countUnreadMessages(conversationId, currentUserId),
                "OK"
        );
    }
}
