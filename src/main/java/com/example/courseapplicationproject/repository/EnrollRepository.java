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

    // Doanh thu theo ngày
    @Query(value = """
    WITH RECURSIVE date_series AS (
        SELECT CURDATE() - INTERVAL :days - 1 DAY AS report_date
        UNION ALL
        SELECT report_date + INTERVAL 1 DAY
        FROM date_series
        WHERE report_date + INTERVAL 1 DAY <= CURDATE()
    )
    SELECT
        DATE_FORMAT(ds.report_date,'%d-%m-%Y') AS date,
        IFNULL(SUM(
            CASE 
                WHEN c.author_id = :teacherId AND (:courseId IS NULL OR c.id = :courseId)
                THEN pd.price
                ELSE 0
            END
        ), 0) AS value
    FROM date_series ds
    LEFT JOIN payment_details pd 
        ON DATE(pd.created_at) = ds.report_date
    LEFT JOIN course c 
        ON pd.course_id = c.id
    LEFT JOIN payment p
        ON pd.payment_id = p.id
    WHERE p.payment_status = 'SUCCESS'
    GROUP BY ds.report_date
    ORDER BY ds.report_date
    """, nativeQuery = true)
    List<PerformanceOverviewProjection> getRevenueByDay(
            @Param("teacherId") Long teacherId,
            @Param("courseId") Long courseId,
            @Param("days") int days
    );

    // Doanh thu theo tháng
    @Query(value = """
    WITH RECURSIVE month_series AS (
        SELECT CURDATE() - INTERVAL :months MONTH AS report_date
        UNION ALL
        SELECT report_date + INTERVAL 1 MONTH
        FROM month_series
        WHERE report_date + INTERVAL 1 MONTH <= CURDATE()
    )
    SELECT
        DATE_FORMAT(ms.report_date, '%m-%Y') AS date,
        IFNULL(SUM(
            CASE 
                WHEN c.author_id = :teacherId AND (:courseId IS NULL OR c.id = :courseId) 
                THEN pd.price
                ELSE 0 
            END
        ), 0) AS value
    FROM month_series ms
    LEFT JOIN payment_details pd 
        ON DATE_FORMAT(pd.created_at, '%m-%Y') = DATE_FORMAT(ms.report_date, '%m-%Y')
    LEFT JOIN course c 
        ON pd.course_id = c.id
    LEFT JOIN payment p
        ON pd.payment_id = p.id
    WHERE p.payment_status = 'SUCCESS'
    GROUP BY ms.report_date
    ORDER BY ms.report_date
    """, nativeQuery = true)
    List<PerformanceOverviewProjection> getRevenueByMonth(
            @Param("teacherId") Long teacherId,
            @Param("courseId") Long courseId,
            @Param("months") int months
    );
    @Query("""
    SELECT SUM(
        CASE
            WHEN p.orderType = 'COURSE' THEN p.totalAmount * 0.5
            WHEN p.orderType = 'AD' THEN p.totalAmount
            ELSE 0
        END
    )
    FROM Payment p
    WHERE p.paymentStatus = 'SUCCESS'
      AND FUNCTION('MONTH', p.createdAt) = FUNCTION('MONTH', CURRENT_DATE)
      AND FUNCTION('YEAR', p.createdAt) = FUNCTION('YEAR', CURRENT_DATE)
""")
    BigDecimal getProfitAdsAndCourseThisMonth();
    @Query("""
    SELECT COUNT(DISTINCT e.user.id)
    FROM Enrollment e
    WHERE FUNCTION('MONTH', e.createdAt) = FUNCTION('MONTH', CURRENT_DATE)
      AND FUNCTION('YEAR', e.createdAt) = FUNCTION('YEAR', CURRENT_DATE)
""")
    Long countUniqueUsersEnrolledThisMonth();
    @Query("""
    SELECT COUNT(c)
    FROM Course c
    WHERE c.status = 'ACCEPTED'
""")
    Long countAcceptedCourses();
    @Query("""
    SELECT COUNT(DISTINCT u)
    FROM User u
    JOIN u.courses c
    WHERE c.status = 'ACCEPTED'
""")
    Long countUsersWithAcceptedCourses();



    @Query("""
    select sum(pd.price)
    from PaymentDetails pd
    where pd.course.author.id = :teacherId
    and pd.payment.paymentStatus = 'SUCCESS'
      and (:courseId IS NULL OR pd.course.id = :courseId)
    """)
    BigDecimal getTotalRevenue(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);


    @Query("""
    select sum(pd.price)
    from PaymentDetails pd
    where pd.course.author.id = :teacherId
    and pd.payment.paymentStatus = 'SUCCESS'
      and (:courseId IS NULL OR pd.course.id = :courseId)
      and MONTH(pd.payment.createdAt) = MONTH(CURRENT_DATE)
      and YEAR(pd.payment.createdAt) = YEAR(CURRENT_DATE)
    """)
    BigDecimal getTotalRevenueThisMonth(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);


    @Query("""
    select count(e)
    from Enrollment e
    where e.course.author.id = :teacherId
      and (:courseId IS NULL OR e.course.id = :courseId)
    """)
    Integer countTotalEnrollmentsByTeacher(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);


    @Query("""
    select count(e)
    from Enrollment e
    where e.course.author.id = :teacherId
      and (:courseId IS NULL OR e.course.id = :courseId)
      and MONTH(e.createdAt) = MONTH(CURRENT_DATE)
      and YEAR(e.createdAt) = YEAR(CURRENT_DATE)
    """)
    Integer countMonthlyEnrollmentsByTeacher(@Param("teacherId") Long teacherId, @Param("courseId") Long courseId);









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
            "c.thumbnail AS thumbnail, " +
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
            "c.thumbnail AS thumbnail, " +
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
    @Query("""
    SELECT SUM(p.totalAmount * 0.5)
    FROM Payment p
    WHERE p.orderType = 'COURSE'
      AND p.paymentStatus = 'SUCCESS'
      AND FUNCTION('MONTH', p.createdAt) = FUNCTION('MONTH', CURRENT_DATE)
      AND FUNCTION('YEAR', p.createdAt) = FUNCTION('YEAR', CURRENT_DATE)
""")
    BigDecimal getCourseProfit();


    @Query("""
    SELECT SUM(p.totalAmount)
    FROM Payment p
    WHERE p.orderType = 'AD'
      AND p.paymentStatus = 'SUCCESS'
      AND FUNCTION('MONTH', p.createdAt) = FUNCTION('MONTH', CURRENT_DATE)
      AND FUNCTION('YEAR', p.createdAt) = FUNCTION('YEAR', CURRENT_DATE)
""")
    BigDecimal getAdProfit();
    @Query("""
    SELECT FUNCTION('MONTH', e.createdAt),
           COUNT(DISTINCT e.user.id)
    FROM Enrollment e
    WHERE e.createdAt >= :startDate
    GROUP BY FUNCTION('MONTH', e.createdAt)
    ORDER BY FUNCTION('MONTH', e.createdAt)
""")
    List<Object[]> countNewEnrollmentsEachMonth(@Param("startDate") LocalDateTime startDate);


}
