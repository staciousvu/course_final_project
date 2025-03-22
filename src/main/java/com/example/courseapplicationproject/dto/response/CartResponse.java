package com.example.courseapplicationproject.dto.response;

import java.util.ArrayList;
import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartResponse {
    //    BigDecimal totalAmount;
    @Builder.Default
    List<CartItemResponse> cartItemResponses = new ArrayList<>();

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class CartItemResponse {
        CourseResponse courseResponse;
        double totalHour;
        int totalLectures;
    }
}
