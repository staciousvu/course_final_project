package com.example.courseapplicationproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdvertisementResponse {
    private Long advertisementId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String packageName;
    int durationDays;
    private LocalDateTime createdAt;
    // Course info
    private Long courseId;
    private String courseTitle;
}
