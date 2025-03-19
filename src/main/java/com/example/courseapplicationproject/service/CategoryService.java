package com.example.courseapplicationproject.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.courseapplicationproject.dto.request.CategoryRequest;
import com.example.courseapplicationproject.dto.response.CategoryBasicResponse;
import com.example.courseapplicationproject.dto.response.CategoryDetailResponse;
import com.example.courseapplicationproject.elasticsearch.document.CategoryDocument;
import com.example.courseapplicationproject.elasticsearch.repository.CategoryElasticRepository;
import com.example.courseapplicationproject.elasticsearch.service.CategoryElasticService;
import com.example.courseapplicationproject.entity.Category;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.mapper.CategoryMapper;
import com.example.courseapplicationproject.repository.CategoryRepository;
import com.example.courseapplicationproject.repository.CourseRepository;
import com.example.courseapplicationproject.repository.UserRepository;
import com.example.courseapplicationproject.service.interfaces.ICategoryService;
import com.example.courseapplicationproject.util.SlugUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CategoryElasticService categoryElasticService;
    private final CategoryElasticRepository categoryElasticRepository;

//    public void saveCategoryElastic(CategoryDocument categoryDocument) {
//        categoryElasticRepository.save(categoryDocument);
//    }

//    @CacheEvict(value = "categories", allEntries = true)
    @Override
    @Transactional
    public CategoryBasicResponse createCategory(CategoryRequest request) {
        log.info("Creating new category: {}", request.getName());

        String newSlug = SlugUtils.generateSlug(request.getName());

        Category category = categoryMapper.toCategory(request);
        category.setSlug(newSlug);

        Category savedCategory = categoryRepository.save(category);
//        saveCategoryElastic(categoryMapper.toCategoryDocument(category));

        log.info("Category created successfully: {}", savedCategory.getId());
        return categoryMapper.toCategoryResponse(savedCategory);
    }

//    @CachePut(value = "category", key = "#result.slug")
    @Override
    @Transactional
    public CategoryBasicResponse updateCategory(Long id, CategoryRequest request) {
        log.info("Updating category with id: {}", id);

        Category category =
                categoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        if (!category.getName().equals(request.getName())) {
            String newSlug = SlugUtils.generateSlug(request.getName());
            category.setSlug(newSlug);
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIsActive(request.getIsActive());
        category.setDisplayOrder(request.getDisplayOrder());

        Category updatedCategory = categoryRepository.save(category);
//        saveCategoryElastic(categoryMapper.toCategoryDocument(category));
        log.info("Category updated successfully: {}", updatedCategory.getId());

        return categoryMapper.toCategoryResponse(updatedCategory);
    }

//    @Caching(
//            evict = {
//                @CacheEvict(value = "categories", allEntries = true),
//                @CacheEvict(value = "activeCategories", allEntries = true)
//            })
    @Override
    @Transactional
    public void deleteCategory(Long id) {
        log.warn("Deleting category with id: {}", id);

        Category category =
                categoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        category.setIsActive(false);
        categoryRepository.save(category);
//        saveCategoryElastic(categoryMapper.toCategoryDocument(category));
        log.warn("Category soft deleted: {}", id);
    }

//    @Cacheable(value = "category", key = "#slug")
    @Override
    public CategoryDetailResponse getCategoryBySlug(String slug) {
        log.info("Fetching category with slug: {}", slug);

        Category category =
                categoryRepository.findBySlug(slug).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        CategoryDetailResponse categoryDetailResponse = categoryMapper.toCategoryDetailResponse(category);
        categoryDetailResponse.setCourseCount(courseRepository.countCourseByCategory_Id(category.getId()));
        categoryDetailResponse.setStudentCount(userRepository.countStudentByCategoryId(category.getId()));
        return categoryDetailResponse;
    }

//    @Cacheable(value = "categories")
    @Override
    public List<CategoryBasicResponse> getAllCategories() {
        log.info("Fetching all categories");

        return categoryRepository.findAllSortedByDisplayOrder().stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

//    @Cacheable(value = "activeCategories")
    @Override
    public List<CategoryBasicResponse> getActiveCategories() {
        return categoryRepository.findByIsActiveTrue().stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryBasicResponse> searchCategories(String keyword) {
        log.info("Searching categories with keyword: {}", keyword);
        List<CategoryDocument> categoryDocuments = categoryElasticService.searchCategory(keyword);
        return categoryDocuments.stream()
                .map(categoryMapper::toCategoryBasicResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryBasicResponse> getCategoriesByParentId(Long parentId) {
        return categoryRepository.findByParentCategory_Id(parentId).stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

//    @CacheEvict(value = "categories", allEntries = true)
    @Override
    @Transactional
    public void updateDisplayOrder(Long id, int displayOrder) {
        Category category =
                categoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        category.setDisplayOrder(displayOrder);
        categoryRepository.save(category);
    }
}
