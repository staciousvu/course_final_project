package com.example.courseapplicationproject.dto.response;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
