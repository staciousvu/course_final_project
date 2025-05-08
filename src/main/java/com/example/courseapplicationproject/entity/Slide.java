package com.example.courseapplicationproject.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Entity
@Table(name = "slide")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Slide extends AbstractEntity<Long> {
    String urlImage;
    Long position;
    Boolean isActive;
}
