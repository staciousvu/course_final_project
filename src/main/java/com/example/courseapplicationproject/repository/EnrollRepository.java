package com.example.courseapplicationproject.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.Enrollment;

import java.util.List;

@Repository
public interface EnrollRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByCourseIdAndUserId(Long courseId, Long userId);
    @Query("select e.course.id from Enrollment e where e.user.id =:userId")
    List<Long> getIdsEnrolledCourseLatestByUserId(@Param("userId") Long userId, Pageable pageable);
}
