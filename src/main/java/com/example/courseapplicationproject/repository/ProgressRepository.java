package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.CourseProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<CourseProgress,Long> {
    @Query("select count(c)>0 from CourseProgress c where c.lecture.id =: lectureId " +
            "and c.user.id =:userId and c.isCompleted = true ")
    boolean existsByLectureIdAndUserId(@Param("lectureId") Long lectureId,@Param("userId") Long userId);
    @Query("select c from CourseProgress c where c.lecture.id in :idsLecture " +
            "and c.isCompleted = true ")
    List<CourseProgress> findAllLectureCompleted(@Param("idsLecture") List<Long> idsLecture);

    Optional<CourseProgress> findByLectureIdAndUserId(Long lectureId, Long userId);
}
