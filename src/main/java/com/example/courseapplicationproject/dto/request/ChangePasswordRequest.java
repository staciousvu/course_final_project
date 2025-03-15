package com.example.courseapplicationproject.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChangePasswordRequest {
    @JsonProperty("old_password")
    String oldPassword;

    @JsonProperty("new_password")
    String newPassword;

    @JsonProperty("confirm_new_password")
    String confirmNewPassword;
}
