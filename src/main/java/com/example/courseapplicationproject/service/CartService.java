package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.response.CartResponse;
import com.example.courseapplicationproject.dto.response.CourseResponse;
import com.example.courseapplicationproject.entity.*;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.mapper.CourseMapper;
import com.example.courseapplicationproject.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class CartService {
    LectureRepository lectureRepository;
    CartRepository cartRepository;
    UserRepository userRepository;
    CourseRepository courseRepository;
    CourseMapper courseMapper;
    VoucherService voucherService;
    FavoriteRepository favoriteRepository;
    public void moveCourseFromFavoriteToCart(Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // 1. Xoá khỏi Favorite nếu có
        favoriteRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .ifPresent(favoriteRepository::delete);

        // 2. Thêm vào Cart nếu chưa có
        boolean exists = cartRepository.existsByUserIdAndCourseId(user.getId(), courseId);
        if (!exists) {
            Cart cart = Cart.builder()
                    .user(user)
                    .course(course)
                    .build();
            cartRepository.save(cart);
        }
    }

    public void moveCourseFromCartToFavorite(Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        // 1. Xoá khỏi giỏ hàng nếu có
        cartRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .ifPresent(cartRepository::delete);

        // 2. Thêm vào Favorite nếu chưa có
        boolean exists = favoriteRepository.existsByUserIdAndCourseId(user.getId(), courseId);
        if (!exists) {
            Favorite favorite = Favorite.builder()
                    .user(user)
                    .course(course)
                    .build();
            favoriteRepository.save(favorite);
        }
    }

    public CartResponse getCart4User() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Cart> cartItems = cartRepository.findAllByUserId(user.getId());

        if (cartItems.isEmpty()) {
            return CartResponse.builder()
                    .cartItemResponses(new ArrayList<>())
                    .build();
        }

        List<Long> courseIds = cartItems.stream()
                .map(cart -> cart.getCourse().getId())
                .toList();

        Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
        Map<Long, Integer> countRatingForCourses = getCountRatings(courseIds);

        List<CartResponse.CartItemResponse> cartItemResponses = cartItems.stream().map(cart -> {
            Course course = cart.getCourse();
            CourseResponse courseResponse = courseMapper.toCourseResponse(course);

            courseResponse.setLevel(course.getLevel().name());
            courseResponse.setStatus(course.getStatus().name());
            courseResponse.setCountRating(countRatingForCourses.getOrDefault(course.getId(), 0));
            courseResponse.setAvgRating(avgRatingForCourses.getOrDefault(course.getId(), 0.0));
            courseResponse.setDiscount_price(voucherService.calculateDiscountedPrice(course.getPrice()));
            courseResponse.setAuthorAvatar(course.getAuthor().getAvatar());
            courseResponse.setPreviewVideo(course.getPreviewVideo());
            courseResponse.setAuthorName(course.getAuthor().getLastName() + " " + course.getAuthor().getFirstName());

            int totalLectures = 0;
            double totalHour = 0.0;
            for (Section section : course.getSections()) {
                totalLectures += section.getLectures().size();
                totalHour += section.getLectures().stream()
                        .mapToDouble(lecture -> lecture.getDuration() / 3600.0)
                        .sum();
            }

            return CartResponse.CartItemResponse.builder()
                    .courseResponse(courseResponse)
                    .totalHour(totalHour)
                    .totalLectures(totalLectures)
                    .build();
        }).toList();

        return CartResponse.builder()
                .cartItemResponses(cartItemResponses)
                .build();
    }

    @Transactional
    public void addCourseToCart(Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        boolean exists = cartRepository.existsByUserIdAndCourseId(user.getId(), courseId);
        if (!exists) {
            Cart cart = Cart.builder()
                    .user(user)
                    .course(course)
                    .build();
            cartRepository.save(cart);
            favoriteRepository.deleteByUserIdAndCourseId(user.getId(),courseId);
        }
    }

    public void removeCourseFromCart(Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        cartRepository.findByUserIdAndCourseId(user.getId(), courseId)
                .ifPresent(cartRepository::delete);
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
