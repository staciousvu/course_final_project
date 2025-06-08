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
public class AdsApplyResponse {
    private Long applyId;

    // Advertisement info
    private Long advertisementId;
    private String packageName;
    private int duration;

    // Course info
    private Long courseId;
    private String courseTitle;
    private String courseThumbnail;

    // Author info
    private Long authorId;
    private String authorName;
    private String authorEmail;
    private String authorAvatar;

    private String status;
}
