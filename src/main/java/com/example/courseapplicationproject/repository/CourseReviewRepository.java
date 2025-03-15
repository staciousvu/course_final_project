package com.example.courseapplicationproject.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.CourseReview;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    @Query("select avg(cr.rating) from CourseReview cr where cr.course.id = :courseId")
    BigDecimal avgRatingByCourse(@Param("courseId") Long courseId);

    @Query("select count(*) from CourseReview cr where cr.course.id = :courseId")
    int countReviewsByCourse(@Param("courseId") Long courseId);
}
