package com.example.courseapplicationproject.dto.response;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
    @JsonProperty("role_name")
    String roleName;

    String description;
    Set<PermissionResponse> permissions;
}
