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
public class DiscussionResponse {
    Long id;
    Long lectureId;  // ID của Lecture mà Discussion này thuộc về
    String content;  // Nội dung thảo luận
    String author;   // Tên người tạo discussion
    LocalDateTime createdAt; // Ngày tạo discussion
    Integer countReplies;
}
