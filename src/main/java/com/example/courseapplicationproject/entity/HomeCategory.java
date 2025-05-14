package com.example.courseapplicationproject.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "home_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HomeCategory extends AbstractEntity<Long> {
    @OneToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;
}

