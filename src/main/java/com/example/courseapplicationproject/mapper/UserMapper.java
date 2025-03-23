package com.example.courseapplicationproject.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.example.courseapplicationproject.dto.request.UpdateProfileRequest;
import com.example.courseapplicationproject.dto.response.UserResponse;
import com.example.courseapplicationproject.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse userToUserResponse(User user);
    void updateUserFromUpdateProfileRequest(UpdateProfileRequest updateProfileRequest, @MappingTarget User user);
}
