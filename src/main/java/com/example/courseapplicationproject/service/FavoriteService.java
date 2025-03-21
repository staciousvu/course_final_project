package com.example.courseapplicationproject.service;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public FavoriteResponse getFavoritesForUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Favorite> favorites = favoriteRepository.findByUserId(user.getId());
        if (favorites.isEmpty()) {
            return FavoriteResponse.builder().favorites(new ArrayList<>()).build();
        }

        List<Long> courseIds = favorites.stream()
                .map(favorite -> favorite.getCourse().getId())
                .toList();

        Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
        Map<Long, Integer> countRatingForCourses = getCountRatings(courseIds);

        List<FavoriteResponse.CourseFavorite> favoriteList = favorites.stream()
                .map(favorite -> {
                    Course course = favorite.getCourse();
                    CourseResponse courseResponse = courseMapper.toCourseResponse(course);

                    courseResponse.setLevel(course.getLevel().name());
                    courseResponse.setStatus(course.getStatus().name());
                    courseResponse.setCountRating(countRatingForCourses.getOrDefault(course.getId(), 0));
                    courseResponse.setAvgRating(avgRatingForCourses.getOrDefault(course.getId(), 0.0));

                    User author = course.getAuthor();
                    courseResponse.setAuthorName(author.getLastName() + " " + author.getFirstName());

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
                })
                .collect(Collectors.toList());

        return FavoriteResponse.builder()
                .favorites(favoriteList)
                .build();
    }

    public void addCourseToFavorites(Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        boolean exists = favoriteRepository.existsByUserIdAndCourseId(user.getId(), courseId);
        if (!exists) {
            Favorite favorite = Favorite.builder()
                    .user(user)
                    .course(course)
                    .build();
            favoriteRepository.save(favorite);
        }
    }

    public void removeCourseFromFavorites(Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        favoriteRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .ifPresent(favoriteRepository::delete);
    }

    public Map<Long, Double> getAverageRatings(List<Long> courseIds) {
        List<Object[]> results = courseRepository.findAverageRatingsForCourses(courseIds);
        return results.stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Double) row[1]));
    }

    public Map<Long, Integer> getCountRatings(List<Long> courseIds) {
        List<Object[]> results = courseRepository.countRatingsForCourses(courseIds);
        return results.stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> ((Number) row[1]).intValue()));
    }
}
