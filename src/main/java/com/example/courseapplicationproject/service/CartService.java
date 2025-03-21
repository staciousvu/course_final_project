package com.example.courseapplicationproject.service;

import com.example.courseapplicationproject.dto.response.CartResponse;
import com.example.courseapplicationproject.dto.response.CourseResponse;
import com.example.courseapplicationproject.entity.*;
import com.example.courseapplicationproject.exception.AppException;
import com.example.courseapplicationproject.exception.ErrorCode;
import com.example.courseapplicationproject.mapper.CourseMapper;
import com.example.courseapplicationproject.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class CartService {
    private final LectureRepository lectureRepository;
    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    UserRepository userRepository;
    CourseRepository courseRepository;
    CourseMapper courseMapper;

    public CartResponse getCart4User() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Optional<Cart> cart = cartRepository.findByUserId(user.getId());
        if (cart.isEmpty())
            return CartResponse.builder()
                    .cartItemResponses(new ArrayList<>())
                    .totalAmount(BigDecimal.ZERO)
                    .build();

        List<CartResponse.CartItemResponse> cartItemResponseList = new ArrayList<>();
        Set<CartItem> cartItems = cart.get().getCartItems();

        List<Long> courseIds = cartItems.stream()
                .map(cartItem -> cartItem.getCourse().getId())
                .toList();

        Map<Long, Double> avgRatingForCourses = getAverageRatings(courseIds);
        Map<Long, Integer> countRatingForCourses = getCountRatings(courseIds);

        BigDecimal totalAmount = cartItems.stream()
                .map(cartItem -> cartItem.getCourse().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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
                .totalAmount(totalAmount)
                .cartItemResponses(cartItemResponseList)
                .build();
    }
    public void addCourseToCart(Long courseId){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Course course =courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        Optional<Cart> cart = cartRepository.findByUserId(user.getId());
        if (cart.isEmpty()){
            Cart newCart = new Cart();
            CartItem cartItem = CartItem.builder()
                    .course(course)
                    .build();
            newCart.getCartItems().add(cartItem);
            newCart.setUser(user);
            cartRepository.save(newCart);
        }else {
            Cart existingCart = cart.get();
            CartItem cartItem = CartItem.builder()
                    .course(course)
                    .build();
            existingCart.getCartItems().add(cartItem);
            existingCart.setUser(user);
            cartRepository.save(existingCart);
        }
    }
    public void removeCourseFromCart(Long courseId){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Course course =courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));
        Optional<Cart> cart = cartRepository.findByUserId(user.getId());
        if (cart.isPresent()){
            Cart existingCart = cart.get();
        }
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
