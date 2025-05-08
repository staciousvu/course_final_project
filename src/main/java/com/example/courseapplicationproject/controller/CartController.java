package com.example.courseapplicationproject.controller;

import com.example.courseapplicationproject.dto.response.ApiResponse;
import com.example.courseapplicationproject.dto.response.CartResponse;
import com.example.courseapplicationproject.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class CartController {
    CartService cartService;

    @GetMapping
    public ApiResponse<CartResponse> getCart() {
        return ApiResponse.success(cartService.getCart4User(), "OK");
    }

    @PostMapping("/add/{courseId}")
    public ApiResponse<Void> addCourseToCart(@PathVariable Long courseId) {
        cartService.addCourseToCart(courseId);
        return ApiResponse.success(null, "Course added to cart successfully");
    }
    @PutMapping("/to-favorite/{courseId}")
    public ApiResponse<Void> moveCartToFavorite(@PathVariable Long courseId) {
        cartService.moveCourseFromCartToFavorite(courseId);
        return ApiResponse.success(null, "Moved to favorite successfully.");
    }
    @PutMapping("/to-cart/{courseId}")
    public ApiResponse<Void> moveFavoriteToCart(@PathVariable Long courseId) {
        cartService.moveCourseFromFavoriteToCart(courseId);
        return ApiResponse.success(null, "Moved to cart successfully.");
    }

    @DeleteMapping("/remove/{courseId}")
    public ApiResponse<Void> removeCourseFromCart(@PathVariable Long courseId) {
        cartService.removeCourseFromCart(courseId);
        return ApiResponse.success(null, "Course removed from cart successfully");
    }
}
