package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.entity.Category;
import com.example.courseapplicationproject.entity.HomeCategory;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.CategoryRepository;
import com.example.courseapplicationproject.repository.HomeCategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class HomeCategoryService {
    CategoryRepository categoryRepository;
    HomeCategoryRepository homeCategoryRepository;
    /**
     * Thêm một HomeCategory dựa trên đối tượng Category
     */
    public void addHomeCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        HomeCategory homeCategory = new HomeCategory();
        homeCategory.setCategory(category);
        homeCategoryRepository.save(homeCategory);
    }

    /**
     * Xóa HomeCategory dựa trên đối tượng Category
     */
    @Transactional
    public void removeHomeCategory(Long categoryId) {
        homeCategoryRepository.deleteByCategoryId(categoryId);
    }
    public List<Long> getAllHomeCategoryId() {
        return homeCategoryRepository.findAll().stream().map(
          homeCategory -> homeCategory.getCategory().getId()
        ).toList();
    }
}
