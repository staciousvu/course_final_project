package com.example.courseapplicationproject.dto.event;

import java.io.Serializable;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageRabbbitMQ implements Serializable {
    public enum MessageType {
        EMAIL,
        SMS
    }

    MessageType messageType;
    String templateHTML;
    String content;
    String subject;
    String recipient;
}
