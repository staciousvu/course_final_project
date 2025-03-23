package com.example.courseapplicationproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReplyResponse {
    Long id;
    String content;  // Nội dung thảo luận
    String author;   // Tên người tạo discussion
    LocalDateTime createdAt; // Ngày tạo discussion
}
