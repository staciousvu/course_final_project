package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.CartItem;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    void deleteByCartAndCourseIdIn(Cart cart, List<Long> courseIds);
}
