package com.example.courseapplicationproject.dto.request;

import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaskAllAsReadNotificationRequest {
    List<Long> idsNotificationRead;
}
