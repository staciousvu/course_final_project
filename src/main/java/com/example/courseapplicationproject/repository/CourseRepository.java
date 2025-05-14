package com.example.courseapplicationproject.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    // Truy vấn danh sách các Course theo keyword (trong title, subtitle và description)
    @Query("SELECT c FROM Course c WHERE " +
            "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.subtitle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Course> findByKeyword(@Param("keyword") String keyword);

    List<Course> findByAuthorIdAndStatus(Long authorId, Course.CourseStatus status);

    @Query("select count(*) from Course c where c.author.id = :authorId")
    int countCourseByByAuthor(@Param("authorId") Long authorId);

    @Query("select c from Course c where c.author.id = :authorId")
    List<Course> findCourseByAuthorId(@Param("authorId") Long authorId);

    @Query("SELECT c FROM Course c " +
            "WHERE c.author.id = :authorId " +
            "AND (:keyword IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY c.updatedAt DESC")
    Page<Course> findCourseByAuthorIdAndKeyword(@Param("authorId") Long authorId,
                                                @Param("keyword") String keyword,
                                                Pageable pageable);


    int countCourseByCategory_Id(@Param("categoryId") Long categoryId);

    @Query("select coalesce(avg(cr.rating), 0.0) from CourseReview cr where cr.course.id = :courseId")
    Double findAverageRatingByCourseId(@Param("courseId") Long courseId);


    Page<Course> findAll(Specification<Course> spec, Pageable pageable);

    @Query(
            "SELECT c.id, COALESCE(AVG(cr.rating), 0) FROM Course c LEFT JOIN CourseReview cr ON c.id=cr.course.id WHERE c.id IN :courseIds GROUP BY c.id")
    List<Object[]> findAverageRatingsForCourses(@Param("courseIds") List<Long> courseIds);

    @Query(
            "SELECT c.id, COUNT(cr.id) FROM Course c LEFT JOIN CourseReview cr ON c.id=cr.course.id WHERE c.id IN :courseIds GROUP BY c.id")
    List<Object[]> countRatingsForCourses(@Param("courseIds") List<Long> courseIds);

    @Query(
            "select c.id,count(e.user.id) from Course c left join Enrollment e on c.id=e.course.id where c.id in :coursesId group by c.id"
    )
    List<Object[]> countEnrolledForCourses(@Param("courseIds") List<Long> coursesIds);

    @Query("SELECT c FROM Course c JOIN Enrollment e ON e.course.id = c.id WHERE e.user.id = :userId order by e.createdAt desc")
    Page<Course> findCoursesForUser(@Param("userId") Long userId, Pageable pageable);


    @Query("""
    SELECT c FROM Course c
    LEFT JOIN c.enrollments e
    LEFT JOIN c.reviews r
    WHERE c.category.id IN :subCategoryIds
      AND c.status = 'ACCEPTED'
      AND c.id NOT IN (
          SELECT ec.course.id FROM Enrollment ec
          WHERE ec.user.id = :userId
      )
    GROUP BY c
    ORDER BY COUNT(DISTINCT e.id) DESC, COALESCE(AVG(r.rating), 0) DESC
""")
    List<Course> findTopCoursesBySubCategoriesExcludeEnrolled(
            @Param("subCategoryIds") List<Long> subCategoryIds,
            @Param("userId") Long userId,
            Pageable pageable
    );


    @Query("""
    SELECT c FROM Course c
    LEFT JOIN c.enrollments e
    LEFT JOIN c.reviews r
    WHERE c.category.id = :categoryId
      AND c.status = 'ACCEPTED'
      AND c.id NOT IN (
          SELECT ec.course.id FROM Enrollment ec
          WHERE ec.user.id = :userId
      )
    GROUP BY c
    ORDER BY COUNT(DISTINCT e.id) DESC, COALESCE(AVG(r.rating), 0) DESC
""")
    List<Course> findTopCoursesByCategoryExcludeEnrolled(
            @Param("categoryId") Long categoryId,
            @Param("userId") Long userId,
            Pageable pageable
    );
    @Query(
            """
		SELECT c FROM Course c
		LEFT JOIN c.enrollments e
		LEFT JOIN c.reviews r
		WHERE c.category.id = :categoryId
		AND c.status = 'ACCEPTED'
		GROUP BY c
		ORDER BY COUNT(DISTINCT e.id) DESC, COALESCE(AVG(r.rating), 0) DESC
	""")
    List<Course> findTopCoursesByCategory(@Param("categoryId") Long categoryId, Pageable pageable);


    @Query("select distinct(c) from Course c " +
            "where c.id in :courseIds " +
            "and c.status='ACCEPTED'" +
            "order by c.createdAt desc")
    List<Course> findCoursesByIds(@Param("courseIds") List<Long> courseIds, Pageable pageable);

    @Query("""
    SELECT c FROM Course c
    WHERE c.category.id IN (
        SELECT c1.category.id FROM Course c1 WHERE c1.id IN :courseIds
    )
    AND c.id NOT IN :courseIds
    AND c.id NOT IN (
        SELECT e.course.id FROM Enrollment e WHERE e.user.id = :userId
    )
    AND c.status = 'ACCEPTED'
    ORDER BY c.createdAt DESC
""")
    List<Course> findCoursesRelatedByCategoryAndExcludeEnrolled(
            @Param("courseIds") List<Long> courseIds,
            @Param("userId") Long userId,
            Pageable pageable
    );


    @Query("SELECT COUNT(c) > 0 FROM Course c WHERE c.category.id = :categoryId")
    boolean existsByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT c FROM Course c WHERE " +
            "(:keyword IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND c.status = :status " +
            "AND c.isActive = 'ACTIVE'")
    Page<Course> findCourseByStatus(@Param("status") Course.CourseStatus status,
                                    @Param("keyword") String keyword,
                                    Pageable pageable);


    Page<Course> findByStatusAndIsActive(Course.CourseStatus status, Course.IsActive isActive, Pageable pageable);


    @Query("SELECT c.author.id, COUNT(c) FROM Course c GROUP BY c.author.id")
    List<Object[]> countCoursesByAuthor();

    @Query("SELECT c.author.id, COUNT(DISTINCT e.user.id) " +
            "FROM Enrollment e JOIN e.course c " +
            "GROUP BY c.author.id")
    List<Object[]> countStudentsByTeacher();

    @Query(
            "SELECT SUM(pd.price) " +
                    "FROM Course c " +
                    "JOIN PaymentDetails pd ON c.id = pd.course.id " +
                    "JOIN Payment p ON p.id = pd.payment.id " +
                    "WHERE p.paymentStatus = 'SUCCESS' " +
                    "AND c.author.id = :teacherId"
    )
    BigDecimal revenueForTeacher(@Param("teacherId") Long teacherId);

    @Query("select c from Course c where c.author.id=:authorId")
    List<Course> findCourseForAuthor(@Param("authorId") Long authorId);
}
