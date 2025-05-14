package com.example.courseapplicationproject.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostResponse {
     Long id;
     String title;
     String content;
     String imageUrl;
     Boolean isPublished;
     String slug;
     String authorFullname;
     String authorEmail;
     String authorAvatar;
}
