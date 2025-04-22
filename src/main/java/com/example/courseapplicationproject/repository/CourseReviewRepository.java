package com.example.courseapplicationproject.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.example.courseapplicationproject.entity.Course;
import com.example.courseapplicationproject.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("select c from CourseReview c where c.user.email =:email and c.course.id =:courseId")
    Optional<CourseReview> findByCourseIdAndUserEmail(@Param("email") String email, @Param("courseId") Long courseId);

    Page<CourseReview> findByCourseId(Long courseId, Pageable pageable);

    boolean existsByCourseAndUser(Course course, User user);

    @Query("SELECT cr FROM CourseReview cr " +
            "JOIN cr.course c " +
            "WHERE c.author.id = :instructorId")
    Page<CourseReview> findReviewForInstructor(@Param("instructorId") Long instructorId,Pageable pageable);

}
