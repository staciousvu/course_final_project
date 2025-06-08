package com.example.courseapplicationproject.entity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "permission")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permission extends AbstractEntity<Integer> {


    @Column(name = "permission_name", nullable = false, unique = true, length = 200)
    String permissionName;

    @Column(name = "description", length = 255)
    String description;
}
