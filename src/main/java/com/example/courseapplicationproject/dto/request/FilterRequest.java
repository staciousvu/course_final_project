package com.example.courseapplicationproject.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterRequest {
    String keyword;
    String language;
    String level;
    Long categoryId;
    Boolean isFree;
    Integer minDuration;
    Integer maxDuration;
    Integer avgRatings;
    Boolean isAccepted;
    String sortBy;
    String sortDirection;
}
