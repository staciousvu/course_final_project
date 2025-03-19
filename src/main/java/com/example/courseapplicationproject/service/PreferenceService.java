package com.example.courseapplicationproject.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.dto.response.PreferenceResponse;
import com.example.courseapplicationproject.entity.Category;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.entity.UserPreferenceRoot;
import com.example.courseapplicationproject.entity.UserPreferenceSub;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.CategoryRepository;
import com.example.courseapplicationproject.repository.UserPreferenceRootRepository;
import com.example.courseapplicationproject.repository.UserPreferenceSubRepository;
import com.example.courseapplicationproject.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
@Slf4j
public class PreferenceService {
    UserPreferenceSubRepository userPreferenceSubRepository;
    UserPreferenceRootRepository userPreferenceRootRepository;
    CategoryRepository categoryRepository;
    UserRepository userRepository;

    public void updateUserPreferences(Long rootCategoryId, List<Long> subCategoryIds) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Category rootCategory = categoryRepository
                .findById(rootCategoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        // Xóa root category cũ nếu có (vì mỗi user chỉ có 1 root category)
        userPreferenceRootRepository.deleteByUserId(user.getId());
        userPreferenceRootRepository.save(new UserPreferenceRoot(rootCategory, user));
        // Xử lý Sub Categories
        userPreferenceSubRepository.deleteAllByUserId(user.getId()); // Xóa subs cũ
        if (!subCategoryIds.isEmpty()) {
            List<Category> subCategories = categoryRepository.findAllById(subCategoryIds);
            if (subCategories.size() != subCategoryIds.size()) {
                throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
            }
            List<UserPreferenceSub> newSubPreferences = subCategories.stream()
                    .map(subCategory -> new UserPreferenceSub(subCategory, user))
                    .collect(Collectors.toList());
            userPreferenceSubRepository.saveAll(newSubPreferences);
        }
    }

    public PreferenceResponse getUserPreference() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Category rootCategory = userPreferenceRootRepository
                .findByUserId(user.getId())
                .map(UserPreferenceRoot::getCategory)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        List<PreferenceResponse.SubCategoryResponse> subCategories =
                userPreferenceSubRepository.findAllByUserId(user.getId()).stream()
                        .map(subCategory -> new PreferenceResponse.SubCategoryResponse(
                                subCategory.getCategory().getId(),
                                subCategory.getCategory().getName()))
                        .toList();
        return PreferenceResponse.builder()
                .rootCategoryId(rootCategory.getId())
                .rootCategoryName(rootCategory.getName())
                .subCategories(subCategories)
                .build();
    }
}
