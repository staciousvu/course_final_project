package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);

    Optional<Favorite> findByUserIdAndCourseId(Long id, Long courseId);

    boolean existsByUserIdAndCourseId(Long id, Long courseId);
}
