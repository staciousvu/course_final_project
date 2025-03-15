package com.example.courseapplicationproject.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import com.example.courseapplicationproject.validator.NameConstraint;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionCreateRequest {
    @JsonProperty("permission_name")
    @NameConstraint(min = 10)
    @NotBlank(message = "Permission cannot blank")
    @Min(value = 5, message = "Permission has at least 5 characters")
    String permissionName;

    @NotBlank(message = "Description cannot blank")
    String description;
}
