package com.example.courseapplicationproject.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.dto.response.CourseResponse;
import com.example.courseapplicationproject.dto.response.RecommendKeywordResponse;
import com.example.courseapplicationproject.dto.response.RecommendLeafsResponse;
import com.example.courseapplicationproject.elasticsearch.service.CourseElasticService;
import com.example.courseapplicationproject.entity.Course;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.entity.UserPreferenceRoot;
import com.example.courseapplicationproject.entity.UserPreferenceSub;
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

    public List<CourseResponse> getRecommendCoursesByPreferenceRoot() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        UserPreferenceRoot userPreferenceRoot =
                userPreferenceRootRepository.findByUserId(user.getId()).orElse(null);
        if (userPreferenceRoot == null) {
            return Collections.emptyList();
        }
        Long rootCategoryId = userPreferenceRoot.getCategory().getId();
        List<Long> subCategoriesIds = categoryRepository.findAllSubCategoryIds(rootCategoryId);

        if (subCategoriesIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Course> courses = courseRepository.findTopCoursesBySubCategories(subCategoriesIds, PageRequest.of(0, 6));
        if (courses.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> courseIds = courses.stream().map(Course::getId).toList();
        Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
        Map<Long, Integer> countRatingForCourses = getCountRatings(courseIds);
        return courses.stream()
                .map(course -> {
                    CourseResponse response = courseMapper.toCourseResponse(course);
                    response.setAvgRating(Optional.ofNullable(avgRatingForCourses.get(course.getId()))
                            .orElse(0.0));
                    response.setCountRating(Optional.ofNullable(countRatingForCourses.get(course.getId()))
                            .orElse(0));
                    response.setAuthorName(course.getAuthor().getFirstName() + " "
                            + course.getAuthor().getLastName());
                    return response;
                })
                .toList();
    }

    public List<RecommendLeafsResponse> getRecommendCoursesByLeafNodesCategory() {
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
        List<RecommendLeafsResponse> recommendLeafsResponses = new ArrayList<>();
        for (Long categoryId : selectedCategories) {
            RecommendLeafsResponse recommendLeafsResponse = new RecommendLeafsResponse();
            Pageable pageable = PageRequest.of(0, 6);
            List<Course> courses = courseRepository.findTopCoursesByCategory(categoryId, pageable);
            recommendLeafsResponse.setCategoryName(
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
                        return response;
                    })
                    .toList();
            recommendLeafsResponse.setCourses(courseResponses);
            recommendLeafsResponses.add(recommendLeafsResponse);
        }

        return recommendLeafsResponses;
    }

    public List<CourseResponse> getRecommendCoursesByUserActivity() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<Long> idsCourseUserActivity = userActivityRepository.findIdsActivityByUserId(user.getId());
        Map<Long, Double> avgRatingForCourses = getAverageRatings(idsCourseUserActivity);
        Map<Long, Integer> countRatingForCourses = getCountRatings(idsCourseUserActivity);
        PageRequest pageRequest = PageRequest.of(0, 6);
        List<Course> courses = courseRepository.findCoursesByIds(idsCourseUserActivity, pageRequest);
        return courses.stream()
                .map(course -> {
                    CourseResponse response = courseMapper.toCourseResponse(course);
                    response.setAvgRating(Optional.ofNullable(avgRatingForCourses.get(course.getId()))
                            .orElse(0.0));
                    response.setCountRating(Optional.ofNullable(countRatingForCourses.get(course.getId()))
                            .orElse(0));
                    response.setAuthorName(course.getAuthor().getFirstName() + " "
                            + course.getAuthor().getLastName());
                    return response;
                })
                .toList();
    }

    public List<RecommendKeywordResponse> getRecommendByUserSearchHistory() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        List<String> keywordLists = searchHistoryRepository.findByUserId(user.getId());
        List<RecommendKeywordResponse> recommendKeywordResponses = new ArrayList<>();
        if (keywordLists.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> sub = (keywordLists.size() > 2) ? keywordLists.subList(0, 2) : keywordLists;
        sub.forEach(keyword -> {
            RecommendKeywordResponse recommendKeywordResponse = new RecommendKeywordResponse();
            recommendKeywordResponse.setKeyword(keyword);
            List<Long> courseIds = courseElasticService.fuzzySearch(keyword).stream()
                    .map(Long::parseLong)
                    .toList();
            Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
            Map<Long, Integer> countRatingForCourses = getCountRatings(courseIds);
            PageRequest pageRequest = PageRequest.of(0, 6);
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
                        return response;
                    })
                    .toList();
            recommendKeywordResponse.setCourses(courseResponses);
            recommendKeywordResponses.add(recommendKeywordResponse);
        });
        return recommendKeywordResponses;
    }

    public List<CourseResponse> getRecommendCoursesByRelatedCoursesEnrolled() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        PageRequest pageRequest = PageRequest.of(0, 2);
        List<Long> idsCoursesEnrolled = enrollRepository.getIdsEnrolledCourseLatestByUserId(user.getId(), pageRequest);
        List<Course> relatedEnrolledCourses =
                courseRepository.findCoursesRelatedByCategory(idsCoursesEnrolled, pageRequest);
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
                    return response;
                })
                .toList();
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
