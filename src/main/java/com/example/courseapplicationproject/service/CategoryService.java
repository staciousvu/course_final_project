package com.example.courseapplicationproject.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
//    private final CategoryElasticRepository categoryElasticRepository;

    //    public void saveCategoryElastic(CategoryDocument categoryDocument) {
    //        categoryElasticRepository.save(categoryDocument);
    //    }

    //    @CacheEvict(value = "categories", allEntries = true)
    public Category getCategoryById(Long id) {
        log.info("get parent id"+ categoryRepository.findById(id).get().getParentCategory().getId());
        return categoryRepository.findById(id).orElse(null);
    }
    @Override
    @Transactional
    public CategoryBasicResponse createCategory(CategoryRequest request) {
        log.info("Creating new category: {}", request.getName());
        String newSlug = SlugUtils.generateSlug(request.getName());
        Category parentCategory = categoryRepository.findById(request.getParentCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        Category category = categoryMapper.toCategory(request);
        category.setParentCategory(parentCategory);
        category.setSlug(newSlug);
        Category savedCategory = categoryRepository.save(category);
        log.info("parent id:"+savedCategory.getParentCategory());
        return categoryMapper.toCategoryResponse(savedCategory);
    }

    //    @CachePut(value = "category", key = "#result.slug")
    @Override
    @Transactional
    public CategoryBasicResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
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
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        boolean hasChildCategories = categoryRepository.existsByParentCategory(category);
        if (hasChildCategories) {
            throw new AppException(ErrorCode.CATEGORY_HAS_CHILDREN);
        }

        boolean hasCourses = courseRepository.existsByCategoryId(id);
        if (hasCourses) {
            throw new AppException(ErrorCode.CATEGORY_HAS_COURSES);
        }

        categoryRepository.delete(category);
    }
    @Transactional
    public void toggleCategoryActive(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        category.setIsActive(!category.getIsActive());
        categoryRepository.save(category);
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
        List<Category> rootCategories = categoryRepository.findRootCategories();
        return rootCategories.stream()
                .map(this::buildCategoryResponse)
                .collect(Collectors.toList());
    }

    // Recursive
    private CategoryBasicResponse buildCategoryResponse(Category category) {
        CategoryBasicResponse response = categoryMapper.toCategoryResponse(category);
        response.setParentCategoryId(category.getParentCategory() != null ? category.getParentCategory().getId() : null);
        List<CategoryBasicResponse> subCategories = category.getSubCategories().stream()
                .map(this::buildCategoryResponse)
                .toList();
        response.setSubCategories(new HashSet<>(subCategories));
        return response;
    }

    //    @Cacheable(value = "activeCategories")
    @Override
    public List<CategoryBasicResponse> getActiveCategories() {
        return categoryRepository.findByParentCategoryIsNullAndIsActiveTrue().stream()
                .map(category -> {
                    CategoryBasicResponse response = categoryMapper.toCategoryResponse(category);
                    response.setSubCategories(getActiveSubCategories(category.getSubCategories()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    private Set<CategoryBasicResponse> getActiveSubCategories(Set<Category> subCategories) {
        return subCategories.stream()
                .filter(Category::getIsActive)
                .map(category -> {
                    CategoryBasicResponse response = categoryMapper.toCategoryResponse(category);
                    response.setSubCategories(getActiveSubCategories(category.getSubCategories()));
                    return response;
                })
                .collect(Collectors.toSet());
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
