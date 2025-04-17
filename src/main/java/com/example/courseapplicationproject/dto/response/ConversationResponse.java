package com.example.courseapplicationproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationResponse {
    private Long id;
    private SimpleUserResponse student;
    private SimpleUserResponse instructor;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
}
