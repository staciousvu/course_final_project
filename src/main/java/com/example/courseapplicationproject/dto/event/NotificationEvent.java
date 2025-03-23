package com.example.courseapplicationproject.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEvent{
    String channel;
    String recipient;
    String templateCode;
    String subject;
    Map<String, Object> param;
}
