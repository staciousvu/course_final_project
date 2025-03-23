package com.example.courseapplicationproject.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category extends AbstractEntity<Long> {
    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description", length = 500)
    String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    Category parentCategory;

    @OneToMany(mappedBy = "parentCategory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    @ToString.Exclude
    Set<Category> subCategories = new HashSet<>();

    @Column(name = "slug", unique = true)
    String slug;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    Boolean isActive = true;

    @Column(name = "display_order", columnDefinition = "INT DEFAULT 0")
    Integer displayOrder = 0;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "category")
    @JsonIgnore
    @ToString.Exclude
    Set<Course> courses = new HashSet<>();
}
