package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.request.CourseReviewRequest;
import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.CourseReviewResponse;
import com.example.courseapplicationproject.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/review")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ReviewController {
    ReviewService reviewService;
    @GetMapping("/instructor/special/{courseId}")
    public ApiResponse<Page<CourseReviewResponse>> getReviewsSpecial(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return ApiResponse.success(reviewService.getReviewsSpecial(courseId,page, size),"OK");
    }
    @GetMapping("/instructor/all-course")
    public ApiResponse<Page<CourseReviewResponse>> getReviewsForAllCourseInstructor(@RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "10") int size){
        return ApiResponse.success(reviewService.getReviewsForInstructor(page, size),"OK");
    }
    @GetMapping("/course/{courseId}")
    public ApiResponse<Page<CourseReviewResponse>> getReviewsForCourse(@PathVariable Long courseId,
                                                                       @RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(reviewService.getReviewsForCourse(courseId, page, size), "OK");
    }

    @PostMapping("/course")
    public ApiResponse<CourseReviewResponse> addReviewForCourse(@RequestBody CourseReviewRequest request) {
        return ApiResponse.success(reviewService.addReviewForCourse(request), "Review added successfully");
    }

    @PutMapping("/course")
    public ApiResponse<CourseReviewResponse> editReviewForCourse(@RequestBody CourseReviewRequest request) {
        return ApiResponse.success(reviewService.editReviewForCourse(request), "Review updated successfully");
    }

    @DeleteMapping("/course/{courseId}")
    public ApiResponse<Void> deleteReviewForCourse(@PathVariable Long courseId) {
        reviewService.deleteReviewForCourse(courseId);
        return ApiResponse.success(null, "Review deleted successfully");
    }
    @GetMapping("/list")
    public ApiResponse<Page<CourseReviewResponse>> getReviews(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(reviewService.getReviews(page, size), "OK");
    }
    @DeleteMapping("/{reviewId}")
    public ApiResponse<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReviewByReviewId(reviewId);
        return ApiResponse.success(null, "Review deleted successfully");
    }
}

