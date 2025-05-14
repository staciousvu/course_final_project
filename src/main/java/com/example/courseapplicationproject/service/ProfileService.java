package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.response.UserResponse;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.entity.UserPreferenceRoot;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.UserPreferenceRootRepository;
import com.example.courseapplicationproject.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
@Slf4j
public class ProfileService {
    UserRepository userRepository;
    UserPreferenceRootRepository userPreferenceRootRepository;
    public UserResponse getProfile(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String categoryPre;
        Optional<UserPreferenceRoot> userPreferenceRoot = userPreferenceRootRepository.findByUserId(user.getId());
        if (userPreferenceRoot.isEmpty()) {
            categoryPre = "";
        }else {
            categoryPre = userPreferenceRoot.get().getCategory().getName();
        }
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .address(user.getAddress())
                .favoriteCategory(categoryPre)
                .email(user.getEmail())
                .country(user.getCountry())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .facebookUrl(user.getFacebookUrl())
                .twitterUrl(user.getTwitterUrl())
                .linkedinUrl(user.getLinkedinUrl())
                .instagramUrl(user.getInstagramUrl())
                .githubUrl(user.getGithubUrl())
                .expertise(user.getExpertise())
                .yearOfExpertise(user.getYearOfExpertise())
                .build();
    }
    public UserResponse getProfile(Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .address(user.getAddress())
                .email(user.getEmail())
                .country(user.getCountry())
                .avatar(user.getAvatar())
                .bio(user.getBio())
                .facebookUrl(user.getFacebookUrl())
                .twitterUrl(user.getTwitterUrl())
                .linkedinUrl(user.getLinkedinUrl())
                .instagramUrl(user.getInstagramUrl())
                .githubUrl(user.getGithubUrl())
                .expertise(user.getExpertise())
                .yearOfExpertise(user.getYearOfExpertise())
                .build();
    }
}
