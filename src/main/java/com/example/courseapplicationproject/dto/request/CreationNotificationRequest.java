package com.example.courseapplicationproject.dto.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreationNotificationRequest {
    @NotBlank
    String title;

    @NotBlank
    String message;

    LocalDateTime startTime;
    LocalDateTime endTime;
}
