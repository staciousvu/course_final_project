package com.example.courseapplicationproject.entity;

import java.util.Set;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "role")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role extends AbstractEntity<Integer> {
    public enum RoleType {
        ADMIN,
        INSTRUCTOR,
        LEARNER
    }
    //
    //    @Id
    //    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //    @Column(name = "id")
    //    Integer id;

    @Column(name = "role_name", nullable = false, unique = true, length = 200)
    String roleName;

    @Column(name = "description", length = 255)
    String description;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JsonIgnore
    Set<Permission> permissions;
}
