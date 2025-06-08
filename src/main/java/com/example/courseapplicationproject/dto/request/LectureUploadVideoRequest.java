package com.example.courseapplicationproject.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LectureUploadVideoRequest {
    Boolean previewable;
    Double duration;
    MultipartFile file;
    Long lectureId;
}
