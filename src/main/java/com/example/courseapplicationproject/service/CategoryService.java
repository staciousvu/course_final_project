package com.example.courseapplicationproject.service;

import java.util.*;
import java.util.stream.Collectors;

import com.example.courseapplicationproject.dto.response.*;
import com.example.courseapplicationproject.entity.AbstractEntity;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.entity.UserPreferenceRoot;
import com.example.courseapplicationproject.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.courseapplicationproject.dto.request.CategoryRequest;
import com.example.courseapplicationproject.elasticsearch.document.CategoryDocument;
import com.example.courseapplicationproject.elasticsearch.service.CategoryElasticService;
import com.example.courseapplicationproject.entity.Category;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.mapper.CategoryMapper;
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
    private final UserPreferenceRootRepository userPreferenceRootRepository;
    private final UserPreferenceSubRepository userPreferenceSubRepository;
//    private final CategoryElasticRepository categoryElasticRepository;

    //    public void saveCategoryElastic(CategoryDocument categoryDocument) {
    //        categoryElasticRepository.save(categoryDocument);
    //    }


    //    @CacheEvict(value = "categories", allEntries = true)
    public Category getCategoryById(Long id) {
        log.info("get parent id"+ categoryRepository.findById(id).get().getParentCategory().getId());
        return categoryRepository.findById(id).orElse(null);
    }
    public void addCategory(String categoryName, Long parentId) {
        Category parentCategory = null;
        if (parentId != null) {
            parentCategory = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        String newSlug = SlugUtils.generateSlug(categoryName);

        Category category = Category.builder()
                .name(categoryName)
                .parentCategory(parentCategory)
                .description("")
                .displayOrder(0)
                .isActive(true)
                .slug(newSlug)
                .build();

        categoryRepository.save(category);
    }
    public SurveyPrefTopicResponse surveyPrefTopicResponse(Long parentId){
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<Long> prefChoiceTopicIds = userPreferenceSubRepository.findAllByUserId(user.getId())
                .stream().map(item->item.getCategory().getId()).toList();
        List<Category> categories = categoryRepository.findAllTopicCategoryIds(parentId);
        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> CategoryDTO.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .slug(category.getSlug())
                        .isActive(category.getIsActive())
                        .displayOrder(category.getDisplayOrder())
                        .build())
                .toList();
        return SurveyPrefTopicResponse.builder()
                .prefChoiceTopicIds(prefChoiceTopicIds)
                .categories(categoryDTOS)
                .build();
    }

    //    @CachePut(value = "category", key = "#result.slug")
    @Override
    @Transactional
    public CategoryBasicResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                        .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        if (request.getName() != null && (!category.getName().equals(request.getName()))) {
            String newSlug = SlugUtils.generateSlug(request.getName());
            category.setSlug(newSlug);
        }
        if (request.getName() != null) {
            category.setName(request.getName());
        }
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

    @Override
    public List<CategoryBasicResponse> searchCategories(String keyword) {
        return List.of();
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


//    @Override
//    public List<CategoryBasicResponse> searchCategories(String keyword) {
//        log.info("Searching categories with keyword: {}", keyword);
//        List<CategoryDocument> categoryDocuments = categoryElasticService.searchCategory(keyword);
//        return categoryDocuments.stream()
//                .map(categoryMapper::toCategoryBasicResponse)
//                .collect(Collectors.toList());
//    }

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
    public List<CategoryDTO> getSubcategories(Long parentId) {
        List<Category> subcategories = categoryRepository.findByParentCategoryId(parentId);
        return subcategories.stream()
                .map(category -> CategoryDTO.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .slug(category.getSlug())
                        .isActive(category.getIsActive())
                        .displayOrder(category.getDisplayOrder())
                        .build())
                .collect(Collectors.toList());
    }
    public List<CategoryDTO> getCategoryHierarchy(Long topicId) {
        List<CategoryDTO> hierarchy = new ArrayList<>();
        Category category = categoryRepository.findById(topicId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        while (category != null) {
            hierarchy.add(0, new CategoryDTO(category.getId(), category.getName(), category.getSlug(),
                    category.getIsActive(), category.getDisplayOrder()));
            category = category.getParentCategory();
        }
        return hierarchy;
    }
    public CategoryRecommendAdminDTO getAllLeafsCategoryByRootCategory() {
        List<Category> rootCategories = categoryRepository.findRootCategories();

        List<CategoryRecommendAdminDTO.RootCategoriesDTO> rootDTOs = rootCategories.stream()
                .map(root -> {
                    // Lấy các leaf categories bên dưới root này
                    List<Category> leafCategories = categoryRepository.findAllTopicCategoryIds(root.getId());

                    // Chuyển danh sách leaf sang DTO
                    List<CategoryRecommendAdminDTO.LeafCategoriesDTO> leafDTOs = leafCategories.stream()
                            .map(leaf -> CategoryRecommendAdminDTO.LeafCategoriesDTO.builder()
                                    .id(leaf.getId())
                                    .name(leaf.getName())
                                    .slug(leaf.getSlug())
                                    .totalCourses(categoryRepository.countCourseByTopicId(leaf.getId()))
                                    .isActive(leaf.getIsActive())
                                    .displayOrder(leaf.getDisplayOrder())
                                    .build())
                            .toList();

                    // Trả về Root DTO
                    return CategoryRecommendAdminDTO.RootCategoriesDTO.builder()
                            .id(root.getId())
                            .name(root.getName())
                            .slug(root.getSlug())
                            .isActive(root.getIsActive())
                            .displayOrder(root.getDisplayOrder())
                            .leafCategories(leafDTOs)
                            .build();
                })
                .toList();

        CategoryRecommendAdminDTO result = new CategoryRecommendAdminDTO();
        result.setRootCategoriesDTOS(rootDTOs);
        return result;
    }

    public List<CategoryDTO> getRootCategories(){
        List<Category> rootCategories = categoryRepository.findRootCategories();
        return rootCategories.stream()
                .map(category -> CategoryDTO.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .slug(category.getSlug())
                        .isActive(category.getIsActive())
                        .displayOrder(category.getDisplayOrder())
                        .build())
                .collect(Collectors.toList());
    }
    public SurveyPrefRootResponse getSurveyPref(){
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Long prefRootId;
        Optional<UserPreferenceRoot> userPreferenceRoot= userPreferenceRootRepository.findByUserId(user.getId());
        prefRootId = userPreferenceRoot.map(pr->pr.getCategory().getId()).orElse(null);
        List<Category> rootCategories = categoryRepository.findRootCategories();
        List<CategoryDTO> categoryDTOS = rootCategories.stream()
                .map(category -> CategoryDTO.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .slug(category.getSlug())
                        .isActive(category.getIsActive())
                        .displayOrder(category.getDisplayOrder())
                        .build())
                .toList();
        return SurveyPrefRootResponse.builder()
                .prefRootId(prefRootId)
                .categories(categoryDTOS)
                .build();
    }


}
