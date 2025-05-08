package com.example.courseapplicationproject.service;

import java.util.*;
import java.util.stream.Collectors;

import com.example.courseapplicationproject.repository.CartRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.dto.response.CourseResponse;
import com.example.courseapplicationproject.dto.response.FavoriteResponse;
import com.example.courseapplicationproject.entity.Course;
import com.example.courseapplicationproject.entity.Favorite;
import com.example.courseapplicationproject.entity.Section;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.mapper.CourseMapper;
import com.example.courseapplicationproject.repository.CourseRepository;
import com.example.courseapplicationproject.repository.FavoriteRepository;
import com.example.courseapplicationproject.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final CartRepository cartRepository;
    private final VoucherService voucherService;

    public Page<FavoriteResponse.CourseFavorite> getFavoritesForUser(String keyword, Integer page, Integer size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size);
        Page<Favorite> favorites = favoriteRepository.searchFavoritesByUserId(user.getId(), keyword, pageable);

        if (favorites.isEmpty()) {
            return Page.empty();
        }

        List<Long> courseIds = favorites.stream()
                .map(favorite -> favorite.getCourse().getId())
                .toList();

        Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
        Map<Long, Integer> countRatingForCourses = getCountRatings(courseIds);

        return favorites.map(favorite -> {
            Course course = favorite.getCourse();
            CourseResponse courseResponse = courseMapper.toCourseResponse(course);

            courseResponse.setLevel(course.getLevel().name());
            courseResponse.setStatus(course.getStatus().name());
            courseResponse.setCountRating(countRatingForCourses.getOrDefault(course.getId(), 0));
            courseResponse.setAvgRating(avgRatingForCourses.getOrDefault(course.getId(), 0.0));

            User author = course.getAuthor();
            courseResponse.setAuthorName(author.getLastName() + " " + author.getFirstName());
            courseResponse.setDiscount_price(voucherService.calculateDiscountedPrice(course.getPrice()));

            int totalLectures = 0;
            double totalHour = 0.0;
            for (Section section : course.getSections()) {
                totalLectures += section.getLectures().size();
                totalHour += section.getLectures().stream()
                        .mapToDouble(lecture -> lecture.getDuration() / 3600.0)
                        .sum();
            }

            return FavoriteResponse.CourseFavorite.builder()
                    .courseResponse(courseResponse)
                    .totalHour(totalHour)
                    .totalLectures(totalLectures)
                    .build();
        });
    }


    @Transactional
    public void addCourseToFavorites(Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Course course =
                courseRepository.findById(courseId).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        boolean exists = favoriteRepository.existsByUserIdAndCourseId(user.getId(), courseId);
        if (!exists) {
            Favorite favorite = Favorite.builder().user(user).course(course).build();
            favoriteRepository.save(favorite);
            cartRepository.deleteByUserIdAndCourseId(user.getId(),courseId);
        }
    }

    public void removeCourseFromFavorites(Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        favoriteRepository.findByUserIdAndCourseId(user.getId(), courseId).ifPresent(favoriteRepository::delete);
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
