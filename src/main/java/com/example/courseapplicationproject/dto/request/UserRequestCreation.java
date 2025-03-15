package com.example.courseapplicationproject.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequestCreation {
    @JsonProperty("first_name")
    @NotBlank(message = "First name can not blank")
    @Size(min = 3, message = "First name must be at least 3 characters.")
    String firstName;

    @JsonProperty("last_name")
    @Size(min = 3, message = "Last name has at least 3 characters.")
    @NotBlank(message = "Last name can not blank.")
    String lastName;

    @Email(message = "Email invalid")
    String email;

    //    @PasswordConstraint(message = "Password must be at least 6 characters long, including at least 1 number and 1
    // special character. ")
    String password;

    @JsonProperty("confirm_password")
    String passwordConfirm;
}
