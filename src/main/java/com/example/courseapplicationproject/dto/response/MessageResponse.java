package com.example.courseapplicationproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageResponse {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderEmail;
    private String senderAvatar;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
