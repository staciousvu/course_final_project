package com.example.courseapplicationproject.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true)
public enum ErrorCode {
    USER_EXISTED(1000, "User already existed", HttpStatus.BAD_REQUEST),
    GENERATE_TOKEN_FAILED(1001, "Generate token failed", HttpStatus.BAD_REQUEST),
    AUTHENTICATION_FAILED(1002, "Authentication failed", HttpStatus.UNAUTHORIZED),
    ACCOUNT_BANNED(1003, "Account banned", HttpStatus.UNAUTHORIZED),
    ROLE_NOT_FOUND(1004, "Role not found", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(1005, "User not found", HttpStatus.NOT_FOUND),
    EXPIRED_TOKEN(1006, "Expired token", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(1007, "Invalid token", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(1008, "Access denied , you can't access this resources", HttpStatus.FORBIDDEN),
    PERMISSION_EXISTED(1009, "Permission already existed", HttpStatus.CONFLICT),
    ROLE_EXISTED(1010, "Role already existed", HttpStatus.CONFLICT),
    PERMISSION_NOT_FOUND(1011, "Permission not found", HttpStatus.NOT_FOUND),
    PASSWORD_CONFIRM_WRONG(1012, "Password confirmation failed", HttpStatus.BAD_REQUEST),
    INVALID_OTP(1013, "Invalid otp", HttpStatus.BAD_REQUEST),
    EXPIRED_OTP(1014, "Expired otp", HttpStatus.BAD_REQUEST),
    OLD_PASSWORD_WRONG(1015, "Old password wrong", HttpStatus.BAD_REQUEST),
    COURSE_NOT_FOUND(1016, "Course not found", HttpStatus.NOT_FOUND),
    DELETE_IMAGE_FAILED(1017, "Delete image failed", HttpStatus.BAD_REQUEST),
    UPLOAD_IMAGE_FAILED(1018, "Upload image failed", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(1019, "Category not found", HttpStatus.NOT_FOUND),
    SLUG_EXISTED(1020, "Slug already existed", HttpStatus.CONFLICT),
    PAYMENT_NOT_FOUND(1021, "Payment not found", HttpStatus.NOT_FOUND),
    COURSE_ALREADY_PURCHASED(1022, "Course already purchased", HttpStatus.CONFLICT),
    COURSE_CANNOT_BE_SUBMITTED(1023, "Course already submitted", HttpStatus.CONFLICT),
    COURSE_CANNOT_BE_ACCEPTED(1024, "The course is not in a pending state", HttpStatus.CONFLICT),
    COURSE_CANNOT_BE_REJECTED(1025, "The course is not in a pending state", HttpStatus.CONFLICT),
    COURSE_CANNOT_BE_DELETED(1026, "The course cannot delete", HttpStatus.BAD_REQUEST),
    SECTION_NOT_FOUND(1027, "Section not found", HttpStatus.NOT_FOUND),
    LECTURE_NOT_FOUND(1028, "Lecture not found", HttpStatus.NOT_FOUND),
    PREFERENCE_NOT_FOUND(1029, "Preference not found", HttpStatus.NOT_FOUND),
    REVIEW_NOT_FOUND(1030, "Review not found", HttpStatus.NOT_FOUND),
    INVALID_RATING(1031, "Invalid rating", HttpStatus.BAD_REQUEST),
    CART_NOT_FOUND(1032, "Cart not found", HttpStatus.NOT_FOUND),
    ;
    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;
}
