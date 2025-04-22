package com.example.courseapplicationproject.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.dto.request.CourseReviewRequest;
import com.example.courseapplicationproject.dto.response.CourseReviewResponse;
import com.example.courseapplicationproject.entity.Course;
import com.example.courseapplicationproject.entity.CourseReview;
import com.example.courseapplicationproject.entity.User;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.repository.CourseRepository;
import com.example.courseapplicationproject.repository.CourseReviewRepository;
import com.example.courseapplicationproject.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
@Slf4j
public class ReviewService {
    CourseRepository courseRepository;
    UserRepository userRepository;
    CourseReviewRepository courseReviewRepository;
    public Page<CourseReviewResponse> getReviewsForInstructor( int page, int size){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CourseReview> reviewPage = courseReviewRepository.findReviewForInstructor(user.getId(),pageable);
        return reviewPage.map(this::mapToResponse);
    }


    public Page<CourseReviewResponse> getReviewsForCourse(Long courseId, int page, int size) {
        Course course =
                courseRepository.findById(courseId).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CourseReview> reviewsPage = courseReviewRepository.findByCourseId(courseId, pageable);

        return reviewsPage.map(this::mapToResponse);
    }
    public Page<CourseReviewResponse> getReviews(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CourseReview> reviewsPage = courseReviewRepository.findAll(pageable);
        return reviewsPage.map(this::mapToResponse);
    }
    public Page<CourseReviewResponse> getReviewsSpecial(Long courseId, int page, int size) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<CourseReview> reviewPage;

        if (courseId == 0) {
            // Lấy tất cả review của instructor
            reviewPage = courseReviewRepository.findReviewForInstructor(user.getId(), pageable);
        } else {
            // Lấy review cho course cụ thể
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

            if (!course.getAuthor().getId().equals(user.getId())) {
                throw new AppException(ErrorCode.ACCESS_DENIED);
            }

            reviewPage = courseReviewRepository.findByCourseId(courseId, pageable);
        }

        return reviewPage.map(this::mapToResponse);
    }


    public CourseReviewResponse addReviewForCourse(CourseReviewRequest courseReviewRequest) {
        Long courseId = courseReviewRequest.getCourseId();
        Integer rating = courseReviewRequest.getRating();
        String review = courseReviewRequest.getReview();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (rating == null || rating < 1 || rating > 5) {
            throw new AppException(ErrorCode.INVALID_RATING);
        }
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Course course =
                courseRepository.findById(courseId).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        boolean hasReviewed = courseReviewRepository.existsByCourseAndUser(course, user);
        if (hasReviewed) {
            throw new AppException(ErrorCode.ALREADY_REVIEWED);
        }
        course.setCountRating(course.getCountRating() + 1);
        CourseReview courseReview = CourseReview.builder()
                .review(review)
                .course(course)
                .user(user)
                .rating(rating)
                .build();
        courseReviewRepository.save(courseReview);
        courseRepository.save(course);
        return mapToResponse(courseReview);
    }

    public CourseReviewResponse editReviewForCourse(CourseReviewRequest courseReviewRequest) {
        Long courseId = courseReviewRequest.getCourseId();
        Integer rating = courseReviewRequest.getRating();
        String review = courseReviewRequest.getReview();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (rating == null || rating < 1 || rating > 5) {
            throw new AppException(ErrorCode.INVALID_RATING);
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        CourseReview courseReview = courseReviewRepository
                .findByCourseIdAndUserEmail(email, courseId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));
        courseReview.setReview(review);
        courseReview.setRating(rating);
        courseReviewRepository.save(courseReview);

        return mapToResponse(courseReview);
    }

    public void deleteReviewForCourse(Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        CourseReview courseReview = courseReviewRepository
                .findByCourseIdAndUserEmail(email, courseId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        courseReviewRepository.delete(courseReview);
    }
    public void deleteReviewByReviewId(Long reviewId) {
        courseReviewRepository.deleteById(reviewId);
    }

    private CourseReviewResponse mapToResponse(CourseReview courseReview) {
        return CourseReviewResponse.builder()
                .id(courseReview.getId())
                .courseName(courseReview.getCourse().getTitle())
                .reviewerName(courseReview.getUser().getFirstName() + " "
                        + courseReview.getUser().getLastName())
                .reviewerAvatar(courseReview.getUser().getAvatar())
                .updatedAt(courseReview.getUpdatedAt())
                .createdAt(courseReview.getCreatedAt())
                .rating(courseReview.getRating())
                .review(courseReview.getReview())
                .build();
    }

}
