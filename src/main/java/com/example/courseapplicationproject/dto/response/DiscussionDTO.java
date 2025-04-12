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
public class DiscussionDTO {
    Long id;
    Long userId;
    String userAvatar;
    String userName;
    String content;
    LocalDateTime createdAt;
}
