package com.example.courseapplicationproject.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilterRequest {
    String keyword;
    List<String> languages;
    String level;
    Long categoryId;
    Boolean isFree;
    Integer minDuration;
    Integer maxDuration;
    Integer avgRatings;
    List<String> sortByList = new ArrayList<>();
    List<String> sortDirectionList = new ArrayList<>();
}

//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//@FieldDefaults(level = AccessLevel.PRIVATE)
//public class FilterRequest {
//    String keyword;
//    String language;
//    String level;
//    Long categoryId;
//    Boolean isFree;
//    Integer minDuration;
//    Integer maxDuration;
//    Integer avgRatings;
//    Boolean isAccepted;
//    String sortBy;
//    String sortDirection;
//}
