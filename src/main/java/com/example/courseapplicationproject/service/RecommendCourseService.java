package com.example.courseapplicationproject.service;

import java.util.*;

import com.example.courseapplicationproject.entity.Course;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.entity.UserPreferenceRoot;
import com.example.courseapplicationproject.entity.UserPreferenceSub;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.mapper.CourseMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.dto.response.CourseResponse;
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

    public List<CourseResponse> getRecommendCoursesByPreferenceRoot() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        UserPreferenceRoot userPreferenceRoot = userPreferenceRootRepository.findByUserId(user.getId())
                .orElse(null);
        if (userPreferenceRoot == null) {
            return Collections.emptyList();
        }
        Long rootCategoryId = userPreferenceRoot.getCategory().getId();
        List<Long> subCategoriesIds = categoryRepository.findSubCategoryIdsByRootCategory(rootCategoryId);

        if (subCategoriesIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Course> courses = courseRepository.findTopCoursesBySubCategories(subCategoriesIds, PageRequest.of(0, 6));
        if (courses.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> courseIds = courses.stream().map(Course::getId).toList();
        Map<Long, Double> avgRatingForCourses = courseRepository.findAverageRatingsForCourses(courseIds);
        Map<Long, Integer> countRatingForCourses = courseRepository.countRatingsForCourses(courseIds);
        return courses.stream().map(course -> {
            CourseResponse response = courseMapper.toCourseResponse(course);
            response.setAvgRating(Optional.ofNullable(avgRatingForCourses.get(course.getId())).orElse(0.0));
            response.setCountRating(Optional.ofNullable(countRatingForCourses.get(course.getId())).orElse(0));
            return response;
        }).toList();
    }
    public List<List<CourseResponse>> getRecommendCoursesByLeafNodesCategory() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<UserPreferenceSub> subList = userPreferenceSubRepository.findAllByUserId(user.getId());
        if (subList.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> subCategoriesIds = subList.stream().map(s->s.getCategory().getId()).toList();
        List<Long> subCategoriesLeafIds = categoryRepository.findLeafCategories(subCategoriesIds);
        if (subCategoriesLeafIds.isEmpty()) {
            return Collections.emptyList();
        }
        // Chỉ lấy tối đa 2 category đầu tiên
        List<Long> selectedCategories = subCategoriesLeafIds.size() > 2
                ? subCategoriesLeafIds.subList(0, 2)
                : subCategoriesLeafIds;
        List<List<CourseResponse>> recommendedCourses = new ArrayList<>();
        for (Long categoryId : selectedCategories) {
            Pageable pageable = PageRequest.of(0, 6);
            List<Course> courses = courseRepository.findTopCoursesByCategory(categoryId, pageable);
            if (courses.isEmpty()) continue; // Bỏ qua nếu không có khóa học
            // Lấy danh sách ID khóa học
            List<Long> courseIds = courses.stream().map(Course::getId).toList();
            // Lấy thông tin đánh giá trung bình và số lượng đánh giá
            Map<Long, Double> avgRatingForCourses = courseRepository.findAverageRatingsForCourses(courseIds);
            Map<Long, Integer> countRatingForCourses = courseRepository.countRatingsForCourses(courseIds);

            List<CourseResponse> courseResponses = courses.stream().map(course -> {
                CourseResponse response = courseMapper.toCourseResponse(course);
                response.setAvgRating(Optional.ofNullable(avgRatingForCourses.get(course.getId())).orElse(0.0));
                response.setCountRating(Optional.ofNullable(countRatingForCourses.get(course.getId())).orElse(0));
                return response;
            }).toList();

            recommendedCourses.add(courseResponses);
        }

        return recommendedCourses;

    }

}
