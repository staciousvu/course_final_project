package com.example.courseapplicationproject.repository;

import java.util.List;

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
            "SELECT c.id, COALESCE(AVG(cr.rating), 0) FROM Course c LEFT JOIN CourseReview cr ON c.id=cr.course.id WHERE c.id IN :courseIds GROUP BY c.id")
    List<Object[]> findAverageRatingsForCourses(@Param("courseIds") List<Long> courseIds);

    @Query(
            "SELECT c.id, COUNT(cr.id) FROM Course c LEFT JOIN CourseReview cr ON c.id=cr.course.id WHERE c.id IN :courseIds GROUP BY c.id")
    List<Object[]> countRatingsForCourses(@Param("courseIds") List<Long> courseIds);

    @Query("select c from Course c join Enrollment e where e.user.id = :userId")
    Page<Course> findCoursesForUser(@Param("userId") Long userId, Pageable pageable);

    @Query(
            """
	SELECT c FROM Course c
	LEFT JOIN c.enrollments e
	LEFT JOIN c.reviews r
	WHERE c.category.id IN :subCategoryIds
	GROUP BY c
	ORDER BY COUNT(DISTINCT e.id) DESC, COALESCE(AVG(r.rating), 0) DESC
""")
    List<Course> findTopCoursesBySubCategories(@Param("subCategoryIds") List<Long> subCategoryIds, Pageable pageable);

    @Query(
            """
		SELECT c FROM Course c
		LEFT JOIN c.enrollments e
		LEFT JOIN c.reviews r
		WHERE c.category.id = :categoryId
		GROUP BY c
		ORDER BY COUNT(DISTINCT e.id) DESC, COALESCE(AVG(r.rating), 0) DESC
	""")
    List<Course> findTopCoursesByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("select distinct(c) from Course c where c.id in :courseIds order by c.createdAt desc")
    List<Course> findCoursesByIds(@Param("courseIds") List<Long> courseIds, Pageable pageable);

    @Query(
            """
	SELECT c FROM Course c
	WHERE c.category.id IN (
		SELECT c1.category.id FROM Course c1 WHERE c1.id IN :courseIds
	)
	AND c.id NOT IN :courseIds
	""")
    List<Course> findCoursesRelatedByCategory(@Param("courseIds") List<Long> courseIds, Pageable pageable);
}
