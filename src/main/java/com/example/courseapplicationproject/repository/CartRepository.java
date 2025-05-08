package com.example.courseapplicationproject.repository;

import java.util.List;
import java.util.Optional;

import com.example.courseapplicationproject.entity.Cart;
import com.example.courseapplicationproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
    void deleteByUserIdAndCourseId(Long userId,Long courseId);

    void deleteByUserId(Long userId);
    Optional<Cart> findByUser(User user);

    List<Cart> findAllByUserId(Long id);

    boolean existsByUserIdAndCourseId(Long id, Long courseId);

    Optional<Cart> findByUserIdAndCourseId(Long id, Long courseId);
}
