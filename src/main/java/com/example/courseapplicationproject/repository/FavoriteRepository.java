package com.example.courseapplicationproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId AND " +
            "(:keyword IS NULL OR LOWER(f.course.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Favorite> searchFavoritesByUserId(@Param("userId") Long userId,
                                           @Param("keyword") String keyword,
                                           Pageable pageable);


    void deleteByUserIdAndCourseId(Long userId,Long courseId);

    Optional<Favorite> findByUserIdAndCourseId(Long id, Long courseId);

    boolean existsByUserIdAndCourseId(Long id, Long courseId);
}
