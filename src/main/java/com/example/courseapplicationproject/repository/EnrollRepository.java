package com.example.courseapplicationproject.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.courseapplicationproject.dto.projection.PerformanceOverviewProjection;
import com.example.courseapplicationproject.dto.projection.StudentEnrollmentProjection;
import com.example.courseapplicationproject.dto.response.StudentEnrollmentDTO;
import com.example.courseapplicationproject.entity.Course;
import lombok.extern.java.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.Enrollment;

@Repository
public interface EnrollRepository extends JpaRepository<Enrollment, Long> {

    @Query("""
        SELECT
            FUNCTION('DATE_FORMAT', pd.createdAt, '%Y-%m-%d') AS date,
            SUM(pd.price * 0.5) AS value
        FROM PaymentDetails pd
        WHERE pd.course.author.id = :teacherId
          AND pd.createdAt >= :startDate
        GROUP BY FUNCTION('DATE_FORMAT', pd.createdAt, '%Y-%m-%d')
        ORDER BY FUNCTION('DATE_FORMAT', pd.createdAt, '%Y-%m-%d')
    """)
    List<PerformanceOverviewProjection> getTeacherRevenueLastXDays(
            @Param("teacherId") Long teacherId,
            @Param("startDate") LocalDateTime startDate
    );

    @Query(value = """
        WITH RECURSIVE date_series AS (
            SELECT CURDATE() - INTERVAL :days DAY AS report_date
            UNION ALL
            SELECT report_date + INTERVAL 1 DAY
            FROM date_series
            WHERE report_date + INTERVAL 1 DAY <= CURDATE()
        )
        SELECT
            DATE_FORMAT(ds.report_date, '%Y-%m-%d') AS date,
            IFNULL(SUM(pd.price * 0.5), 0) AS value
        FROM date_series ds
        LEFT JOIN payment_details pd ON DATE(pd.created_at) = ds.report_date
        LEFT JOIN course c ON pd.course_id = c.id
        WHERE c.author_id = :teacherId
          AND (:courseId IS NULL OR c.id = :courseId)
        GROUP BY ds.report_date
        ORDER BY ds.report_date
        """,
            nativeQuery = true)
    List<PerformanceOverviewProjection> getTeacherRevenueLastXDaysNative(
            @Param("teacherId") Long teacherId,
            @Param("courseId") Long courseId,
            @Param("days") int days
    );

    boolean existsByCourseIdAndUserId(Long courseId, Long userId);

    @Query("select e.course.id from Enrollment e where e.user.id =:userId order by e.createdAt desc")
    List<Long> getIdsEnrolledCourseLatestByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(e) FROM Enrollment e " +
            "WHERE e.course.author.id = :instructorId")
    Integer countTotalStudents(@Param("instructorId") Long instructorId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId")
    Integer countStudentsByCourse(@Param("courseId") Long courseId);

    @Query("select count(e.id) from Enrollment e where e.course.id=:courseId")
    Integer countEnrolledForCourse(@Param("courseId") Long courseId);


    @Query("select e.course.id from Enrollment e where e.user.id=:userId")
    List<Long> findCourseIdsByUserId(@Param("userId") Long userId);

    List<Enrollment> findByCourseId(Long courseId);

    @Query("select count(e)>0 from Enrollment e where e.user.id =:studentId and e.course.author.id=:instructorId")
    boolean checkEnrolledCourseOfInstructor(@Param("instructorId") Long instructorId,@Param("studentId") Long studentId);

    @Query("SELECT " +
            "u.id AS userId, " +
            "CONCAT(u.firstName, ' ', u.lastName) AS fullName, " +
            "u.email AS email, " +
            "e.id AS enrollmentId, " +
            "e.createdAt AS enrolledOn, " +
            "c.id AS courseId, " +
            "c.title AS courseTitle, " +
            "(SELECT COUNT(cp) FROM CourseProgress cp WHERE cp.user.id = u.id AND cp.course.id = c.id AND cp.isCompleted = true) AS lessonsCompleted, " +
            "(SELECT COUNT(l) FROM Lecture l JOIN l.section s WHERE s.course.id = c.id) AS totalLessons, " +
            "CASE WHEN (SELECT COUNT(cp) FROM CourseProgress cp WHERE cp.user.id = u.id AND cp.course.id = c.id AND cp.isCompleted = true) = " +
            "(SELECT COUNT(l) FROM Lecture l JOIN l.section s WHERE s.course.id = c.id) THEN true ELSE false END AS isCompleted, " +
            "(SELECT cr.rating FROM CourseReview cr WHERE cr.user.id = u.id AND cr.course.id = c.id) AS rating, " +
            "(SELECT COUNT(d) FROM Discussion d WHERE d.user.id = u.id AND d.course.id = c.id) AS questions " +
            "FROM User u " +
            "JOIN u.enrollments e " +
            "JOIN e.course c " +
            "WHERE c.author.id = :instructorId " +
            "AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:status = 'All' OR " +
            "(:status = 'Completed' AND (SELECT COUNT(cp) FROM CourseProgress cp WHERE cp.user.id = u.id AND cp.course.id = c.id AND cp.isCompleted = true) = " +
            "(SELECT COUNT(l) FROM Lecture l JOIN l.section s WHERE s.course.id = c.id)) OR " +
            "(:status = 'In Progress' AND (SELECT COUNT(cp) FROM CourseProgress cp WHERE cp.user.id = u.id AND cp.course.id = c.id AND cp.isCompleted = true) < " +
            "(SELECT COUNT(l) FROM Lecture l JOIN l.section s WHERE s.course.id = c.id)))")
    Page<StudentEnrollmentProjection> findStudentEnrollmentsByInstructorId(@Param("instructorId") Long instructorId,
                                                                           @Param("search") String search,
                                                                           @Param("status") String status,
                                                                           Pageable pageable);

    @Query("SELECT " +
            "u.id AS userId, " +
            "CONCAT(u.firstName, ' ', u.lastName) AS fullName, " +
            "u.email AS email, " +
            "e.id AS enrollmentId, " +
            "e.createdAt AS enrolledOn, " +
            "c.id AS courseId, " +
            "c.title AS courseTitle, " +
            "(SELECT COUNT(cp) FROM CourseProgress cp WHERE cp.user.id = u.id AND cp.course.id = :courseId AND cp.isCompleted = true) AS lessonsCompleted, " +
            "(SELECT COUNT(l) FROM Lecture l JOIN l.section s WHERE s.course.id = :courseId) AS totalLessons, " +
            "CASE WHEN (SELECT COUNT(cp) FROM CourseProgress cp WHERE cp.user.id = u.id AND cp.course.id = :courseId AND cp.isCompleted = true) = " +
            "(SELECT COUNT(l) FROM Lecture l JOIN l.section s WHERE s.course.id = :courseId) THEN true ELSE false END AS isCompleted, " +
            "(SELECT cr.rating FROM CourseReview cr WHERE cr.user.id = u.id AND cr.course.id = :courseId) AS rating, " +
            "(SELECT COUNT(d) FROM Discussion d WHERE d.user.id = u.id AND d.course.id = :courseId) AS questions " +
            "FROM User u " +
            "JOIN u.enrollments e " +
            "JOIN e.course c " +
            "WHERE e.course.id = :courseId " +
            "AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:status = 'All' OR " +
            "(:status = 'Completed' AND (SELECT COUNT(cp) FROM CourseProgress cp WHERE cp.user.id = u.id AND cp.course.id = :courseId AND cp.isCompleted = true) = " +
            "(SELECT COUNT(l) FROM Lecture l JOIN l.section s WHERE s.course.id = :courseId)) OR " +
            "(:status = 'In Progress' AND (SELECT COUNT(cp) FROM CourseProgress cp WHERE cp.user.id = u.id AND cp.course.id = :courseId AND cp.isCompleted = true) < " +
            "(SELECT COUNT(l) FROM Lecture l JOIN l.section s WHERE s.course.id = :courseId)))")
    Page<StudentEnrollmentProjection> findStudentEnrollmentsByCourseId(@Param("courseId") Long courseId,
                                                                           @Param("search") String search,
                                                                           @Param("status") String status,
                                                                           Pageable pageable);
}
