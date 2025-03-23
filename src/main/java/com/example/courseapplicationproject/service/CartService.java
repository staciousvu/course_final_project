package com.example.courseapplicationproject.service;

import java.util.*;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.courseapplicationproject.dto.response.CartResponse;
import com.example.courseapplicationproject.dto.response.CourseResponse;
import com.example.courseapplicationproject.entity.*;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.mapper.CourseMapper;
import com.example.courseapplicationproject.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Slf4j
public class CartService {
    LectureRepository lectureRepository;
    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    UserRepository userRepository;
    CourseRepository courseRepository;
    CourseMapper courseMapper;

    public CartResponse getCart4User() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Optional<Cart> cart = cartRepository.findByUserId(user.getId());
        if (cart.isEmpty())
            return CartResponse.builder()
                    .cartItemResponses(new ArrayList<>())
                    .build();

        List<CartResponse.CartItemResponse> cartItemResponseList = new ArrayList<>();
        Set<CartItem> cartItems = cart.get().getCartItems();

        List<Long> courseIds =
                cartItems.stream().map(cartItem -> cartItem.getCourse().getId()).toList();

        Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
        Map<Long, Integer> countRatingForCourses = getCountRatings(courseIds);

        cartItems.forEach(cartItem -> {
            Course course = cartItem.getCourse();
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

            CartResponse.CartItemResponse cartItemResponse = new CartResponse.CartItemResponse();
            cartItemResponse.setTotalHour(totalHour);
            cartItemResponse.setTotalLectures(totalLectures);
            cartItemResponse.setCourseResponse(courseResponse);

            cartItemResponseList.add(cartItemResponse);
        });

        return CartResponse.builder()
                .cartItemResponses(cartItemResponseList)
                .build();
    }

    public void addCourseToCart(Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Course course =
                courseRepository.findById(courseId).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        Cart cart = cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart newCart = Cart.builder().user(user).cartItems(new HashSet<>()).build();
            return cartRepository.save(newCart);
        });

        boolean exists = cart.getCartItems().stream()
                .anyMatch(item -> item.getCourse().getId().equals(courseId));

        if (!exists) {
            CartItem cartItem = CartItem.builder().cart(cart).course(course).build();
            cart.getCartItems().add(cartItem);
            cartRepository.save(cart);
        }
    }

    public void removeCourseFromCart(Long courseId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Course course =
                courseRepository.findById(courseId).orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        cartRepository.findByUserId(user.getId()).ifPresent(cart -> {
            CartItem cartItem = cart.getCartItems().stream()
                    .filter(item -> item.getCourse().getId().equals(courseId))
                    .findFirst()
                    .orElse(null);
            if (cartItem != null) {
                cart.getCartItems().remove(cartItem);
                cartItemRepository.delete(cartItem);
                cartRepository.save(cart);
            }
        });
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
