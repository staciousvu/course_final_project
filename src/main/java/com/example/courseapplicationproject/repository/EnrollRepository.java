package com.example.courseapplicationproject.repository;

import java.math.BigDecimal;
import java.util.List;

import com.example.courseapplicationproject.entity.Course;
import lombok.extern.java.Log;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.Enrollment;

@Repository
public interface EnrollRepository extends JpaRepository<Enrollment, Long> {
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


    List<Long> findCourseIdsByUserId(Long id);
}
