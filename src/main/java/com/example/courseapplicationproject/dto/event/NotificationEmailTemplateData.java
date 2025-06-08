package com.example.courseapplicationproject.dto.event;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationEmailTemplateData {
    String messageTitle;
    String messageBody;
    String actionLabel;
    String actionUrl;
    String companyName;
    String courseImage;
    String recipient;
    String reason;
    String template;
}
