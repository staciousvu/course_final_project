package com.example.courseapplicationproject.repository;

import java.util.Optional;

import com.example.courseapplicationproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
    void deleteByUserId(Long userId);

    Optional<Cart> findByUser(User user);
}
