package com.example.courseapplicationproject.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtpVerifyRequest {
    @Email(message = "Email invalid")
    String email;

    @Size(min = 6, max = 6, message = "Otp have to 6 characters")
    String otp;
}
