package com.example.courseapplicationproject.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("select count(*) from Course c where c.author.id = :authorId")
    int countCourseByByAuthor(@Param("authorId") Long authorId);

    @Query("select c from Course c where c.author.id = :authorId")
    List<Course> findCourseByAuthorId(@Param("authorId") Long authorId);

    int countCourseByCategory_Id(@Param("categoryId") Long categoryId);

    @Query("select avg(cr.rating) from CourseReview cr where cr.course.id = :courseId")
    double findAverageRatingByCourseId(@Param("courseId") Long courseId);

    Page<Course> findAll(Specification<Course> spec, Pageable pageable);

    @Query(
            "SELECT c.id, COALESCE(AVG(r.rating), 0) FROM Course c LEFT JOIN c.reviews r WHERE c.id IN :courseIds GROUP BY c.id")
    Map<Long, Double> findAverageRatingsForCourses(@Param("courseIds") List<Long> courseIds);

    @Query("SELECT c.id, COUNT(r.id) FROM Course c LEFT JOIN c.reviews r WHERE c.id IN :courseIds GROUP BY c.id")
    Map<Long, Integer> countRatingsForCourses(@Param("courseIds") List<Long> courseIds);

    @Query("select c from Course c join Enrollment e where e.user.id = :userId")
    Page<Course> findCoursesForUser(@Param("userId") Long userId, Pageable pageable);
}
