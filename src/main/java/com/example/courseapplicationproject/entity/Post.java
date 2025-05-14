package com.example.courseapplicationproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "post")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Post extends AbstractEntity<Long>{
    @Column(nullable = false)
    String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    String content;

    String imageUrl;

    @Column(nullable = false)
    Boolean isPublished;

    @ManyToOne
    @JoinColumn(name = "author_id")
    User author;

    @Column(unique = true)
    String slug; // ví dụ: "huong-dan-spring-boot"

    int view;

}
