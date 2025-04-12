package com.example.courseapplicationproject.service;

import java.util.*;
import java.util.stream.Collectors;

import com.example.courseapplicationproject.dto.response.RecommendCourseCategoryRoot;
import com.example.courseapplicationproject.entity.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.dto.response.CourseResponse;
import com.example.courseapplicationproject.dto.response.RecommendCourseKeyword;
import com.example.courseapplicationproject.dto.response.RecommendCourseCategoryLeafs;
import com.example.courseapplicationproject.elasticsearch.service.CourseElasticService;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.mapper.CourseMapper;
import com.example.courseapplicationproject.repository.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
@Slf4j
public class RecommendCourseService {
    UserRepository userRepository;
    CourseRepository courseRepository;
    UserPreferenceSubRepository userPreferenceSubRepository;
    UserPreferenceRootRepository userPreferenceRootRepository;
    CategoryRepository categoryRepository;
    CourseMapper courseMapper;
    UserActivityRepository userActivityRepository;
    CourseElasticService courseElasticService;
    SearchHistoryRepository searchHistoryRepository;
    EnrollRepository enrollRepository;
    VoucherService voucherService;
    CourseContentService courseContentService;

    public RecommendCourseCategoryRoot getRecommendCoursesByPreferenceRoot() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        UserPreferenceRoot userPreferenceRoot =
                userPreferenceRootRepository.findByUserId(user.getId()).orElse(null);
        if (userPreferenceRoot == null) {
            return RecommendCourseCategoryRoot.builder()
                    .categoryRoot(null)
                    .courses(Collections.emptyList())
                    .build();
        }
        Long rootCategoryId = userPreferenceRoot.getCategory().getId();
        List<Long> subCategoriesIds = categoryRepository.findAllSubCategoryIds(rootCategoryId);

        if (subCategoriesIds.isEmpty()) {
            return RecommendCourseCategoryRoot.builder()
                    .categoryRoot(null)
                    .courses(Collections.emptyList())
                    .build();
        }
        List<Course> courses = courseRepository.findTopCoursesBySubCategories(subCategoriesIds, PageRequest.of(0, 5));
        if (courses.isEmpty()) {
            return RecommendCourseCategoryRoot.builder()
                    .categoryRoot(null)
                    .courses(Collections.emptyList())
                    .build();
        }
        List<Long> courseIds = courses.stream().map(Course::getId).toList();
        Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
        Map<Long, Integer> countRatingForCourses = getCountRatings(courseIds);
        return RecommendCourseCategoryRoot.builder()
                .categoryRoot(userPreferenceRoot.getCategory().getName())
                .courses(courses.stream()
                        .map(course -> {
                            CourseResponse response = courseMapper.toCourseResponse(course);
                            response.setAvgRating(Optional.ofNullable(avgRatingForCourses.get(course.getId()))
                                    .orElse(0.0));
                            response.setCountRating(Optional.ofNullable(countRatingForCourses.get(course.getId()))
                                    .orElse(0));
                            response.setAuthorName(course.getAuthor().getFirstName() + " "
                                    + course.getAuthor().getLastName());
                            response.setAuthorAvatar(course.getAuthor().getAvatar());
                            response.setPreviewVideo(course.getPreviewVideo());
                            response.setContents(courseContentService.getAllContents(course.getId())); //course content
                            response.setDiscount_price(voucherService.calculateDiscountedPrice(course.getPrice()));
                            return response;
                        })
                        .toList())
                .build();

    }

    public List<RecommendCourseCategoryLeafs> getRecommendCoursesByLeafNodesCategory() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<UserPreferenceSub> subList = userPreferenceSubRepository.findAllByUserId(user.getId());
        if (subList.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> subCategoriesIds =
                subList.stream().map(s -> s.getCategory().getId()).toList();
        List<Long> subCategoriesLeafIds = categoryRepository.findLeafCategories(subCategoriesIds);
        if (subCategoriesLeafIds.isEmpty()) {
            return Collections.emptyList();
        }
        // Chỉ lấy tối đa 2 category đầu tiên
        List<Long> selectedCategories =
                subCategoriesLeafIds.size() > 2 ? subCategoriesLeafIds.subList(0, 2) : subCategoriesLeafIds;
        List<RecommendCourseCategoryLeafs> recommendCourseCategoryLeafsRespons = new ArrayList<>();
        for (Long categoryId : selectedCategories) {
            RecommendCourseCategoryLeafs recommendCourseCategoryLeafs = new RecommendCourseCategoryLeafs();
            Pageable pageable = PageRequest.of(0, 5);
            List<Course> courses = courseRepository.findTopCoursesByCategory(categoryId, pageable);
            recommendCourseCategoryLeafs.setCategoryName(
                    courses.getFirst().getCategory().getName());
            if (courses.isEmpty()) continue; // Bỏ qua nếu không có khóa học
            // Lấy danh sách ID khóa học
            List<Long> courseIds = courses.stream().map(Course::getId).toList();
            // Lấy thông tin đánh giá trung bình và số lượng đánh giá
            Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
            Map<Long, Integer> countRatingForCourses = getCountRatings(courseIds);

            List<CourseResponse> courseResponses = courses.stream()
                    .map(course -> {
                        CourseResponse response = courseMapper.toCourseResponse(course);
                        response.setAvgRating(Optional.ofNullable(avgRatingForCourses.get(course.getId()))
                                .orElse(0.0));
                        response.setCountRating(Optional.ofNullable(countRatingForCourses.get(course.getId()))
                                .orElse(0));
                        response.setAuthorName(course.getAuthor().getFirstName() + " "
                                + course.getAuthor().getLastName());
                        response.setAuthorAvatar(course.getAuthor().getAvatar());
                        response.setContents(courseContentService.getAllContents(course.getId())); //course content
                        response.setPreviewVideo(course.getPreviewVideo());
                        response.setDiscount_price(voucherService.calculateDiscountedPrice(course.getPrice()));
                        return response;
                    })
                    .toList();
            recommendCourseCategoryLeafs.setCourses(courseResponses);
            recommendCourseCategoryLeafsRespons.add(recommendCourseCategoryLeafs);
        }
        return recommendCourseCategoryLeafsRespons;
    }

    public RecommendCourseCategoryRoot getRecommendCoursesByUserActivity() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<UserActivity> userActivities = userActivityRepository.findIdsActivityByUserId(user.getId());
        Category category = userActivities.getFirst().getCourse().getCategory();
        Long selectedCategoryId = userActivities.getFirst().getCourse().getCategory().getId();
        Pageable pageable = PageRequest.of(0, 5);
        List<Course> courseslist = courseRepository.findTopCoursesByCategory(selectedCategoryId,pageable);
        List<Long> ids = courseslist.stream().map(Course::getId).toList();
        Map<Long, Double> avgRatingForCourses = getAverageRatings(ids);
        Map<Long, Integer> countRatingForCourses = getCountRatings(ids);
        List<CourseResponse> courseResponseList = courseslist.stream()
                .map(course -> {
                    CourseResponse response = courseMapper.toCourseResponse(course);
                    response.setAvgRating(Optional.ofNullable(avgRatingForCourses.get(course.getId()))
                            .orElse(0.0));
                    response.setCountRating(Optional.ofNullable(countRatingForCourses.get(course.getId()))
                            .orElse(0));
                    response.setAuthorName(course.getAuthor().getFirstName() + " "
                            + course.getAuthor().getLastName());
                    response.setContents(courseContentService.getAllContents(course.getId())); //course content
                    response.setPreviewVideo(course.getPreviewVideo());
                    response.setDiscount_price(voucherService.calculateDiscountedPrice(course.getPrice()));
                    return response;
                })
                .toList();
        return RecommendCourseCategoryRoot.builder()
                .courses(courseResponseList)
                .categoryRoot(category.getName())
                .build();
    }

    public List<RecommendCourseKeyword> getRecommendByUserSearchHistory() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<String> keywordLists = searchHistoryRepository.findByUserId(user.getId());
        List<RecommendCourseKeyword> recommendCourseKeywordRespons = new ArrayList<>();
        if (keywordLists.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> sub = (keywordLists.size() > 2) ? keywordLists.subList(0, 2) : keywordLists;
        sub.forEach(keyword -> {
            RecommendCourseKeyword recommendCourseKeyword = new RecommendCourseKeyword();
            recommendCourseKeyword.setKeyword(keyword);
            List<Long> courseIds = courseElasticService.fuzzySearch(keyword).stream()
                    .map(Long::parseLong)
                    .toList();
            Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
            Map<Long, Integer> countRatingForCourses = getCountRatings(courseIds);
            PageRequest pageRequest = PageRequest.of(0, 5);
            List<Course> courses = courseRepository.findCoursesByIds(courseIds, pageRequest);
            List<CourseResponse> courseResponses = courses.stream()
                    .map(course -> {
                        CourseResponse response = courseMapper.toCourseResponse(course);
                        response.setAvgRating(Optional.ofNullable(avgRatingForCourses.get(course.getId()))
                                .orElse(0.0));
                        response.setCountRating(Optional.ofNullable(countRatingForCourses.get(course.getId()))
                                .orElse(0));
                        response.setAuthorName(course.getAuthor().getFirstName() + " "
                                + course.getAuthor().getLastName());
                        response.setContents(courseContentService.getAllContents(course.getId())); //course content
                        response.setPreviewVideo(course.getPreviewVideo());
                        response.setDiscount_price(voucherService.calculateDiscountedPrice(course.getPrice()));
                        return response;
                    })
                    .toList();
            recommendCourseKeyword.setCourses(courseResponses);
            recommendCourseKeywordRespons.add(recommendCourseKeyword);
        });
        return recommendCourseKeywordRespons;
    }

    public List<CourseResponse> getRecommendCoursesByRelatedCoursesEnrolled() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        PageRequest pageRequest = PageRequest.of(0, 2);
        List<Long> idsCoursesEnrolled = enrollRepository.getIdsEnrolledCourseLatestByUserId(user.getId(), pageRequest);
        PageRequest pageRequestRelated = PageRequest.of(0, 5);
        List<Course> relatedEnrolledCourses =
                courseRepository.findCoursesRelatedByCategory(idsCoursesEnrolled, pageRequestRelated);
        List<Long> ids = relatedEnrolledCourses.stream().map(Course::getId).collect(Collectors.toList());
        Map<Long, Double> avgRatingForCourses = getAverageRatings(ids);
        Map<Long, Integer> countRatingForCourses = getCountRatings(ids);
        return relatedEnrolledCourses.stream()
                .map(course -> {
                    CourseResponse response = courseMapper.toCourseResponse(course);
                    response.setAvgRating(Optional.ofNullable(avgRatingForCourses.get(course.getId()))
                            .orElse(0.0));
                    response.setCountRating(Optional.ofNullable(countRatingForCourses.get(course.getId()))
                            .orElse(0));
                    response.setAuthorName(course.getAuthor().getFirstName() + " "
                            + course.getAuthor().getLastName());
                    response.setContents(courseContentService.getAllContents(course.getId())); //course content
                    response.setPreviewVideo(course.getPreviewVideo());
                    response.setDiscount_price(voucherService.calculateDiscountedPrice(course.getPrice()));
                    return response;
                })
                .toList();
    }
    public RecommendCourseCategoryRoot recommendbyCategory(Long categoryId) {
        Category category=categoryRepository.findById(categoryId).orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        PageRequest pageRequest = PageRequest.of(0, 5);
        List<Course> courses = courseRepository.findTopCoursesByCategory(categoryId, pageRequest);
        List<Long> ids = courses.stream().map(Course::getId).collect(Collectors.toList());
        Map<Long, Double> avgRatingForCourses = getAverageRatings(ids);
        Map<Long, Integer> countRatingForCourses = getCountRatings(ids);
        List<CourseResponse> courseResponses = courses.stream()
                .map(course -> {
                    CourseResponse response = courseMapper.toCourseResponse(course);
                    response.setAvgRating(Optional.ofNullable(avgRatingForCourses.get(course.getId()))
                            .orElse(0.0));
                    response.setCountRating(Optional.ofNullable(countRatingForCourses.get(course.getId()))
                            .orElse(0));
                    response.setAuthorName(course.getAuthor().getFirstName() + " "
                            + course.getAuthor().getLastName());
                    response.setContents(courseContentService.getAllContents(course.getId())); //course content
                    response.setPreviewVideo(course.getPreviewVideo());
                    response.setDiscount_price(voucherService.calculateDiscountedPrice(course.getPrice()));
                    return response;
                })
                .toList();
        return RecommendCourseCategoryRoot.builder()
                .courses(courseResponses)
                .categoryRoot(category.getName())
                .build();
    }

    public Map<Long, Double> getAverageRatings(List<Long> courseIds) {
        List<Object[]> results = courseRepository.findAverageRatingsForCourses(courseIds);
        return results.stream().collect(Collectors.toMap(row -> (Long) row[0], row -> (Double) row[1]));
    }

    public Map<Long, Integer> getCountRatings(List<Long> courseIds) {
        List<Object[]> results = courseRepository.countRatingsForCourses(courseIds);
        return results.stream().collect(Collectors.toMap(row -> (Long) row[0], row -> ((Number) row[1]).intValue()));
    }
}
