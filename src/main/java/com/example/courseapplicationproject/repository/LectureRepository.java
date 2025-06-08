package com.example.courseapplicationproject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.Lecture;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    @Query("SELECT COALESCE(SUM(l.duration), 0) FROM Lecture l WHERE l.contentUrl IS NOT NULL and l.courseId = :courseId")
    double getDurationForCourse(@Param("courseId") Long courseId);

    @Query("SELECT COALESCE(COUNT(l), 0) FROM Lecture l WHERE l.contentUrl IS NOT NULL and l.courseId = :courseId")
    int getTotalVideoForCourse(@Param("courseId") Long courseId);

    @Query("SELECT COALESCE(COUNT(l), 0) FROM Lecture l WHERE l.documentUrl IS NOT NULL AND l.courseId = :courseId")
    int getTotalDocumentForCourse(@Param("courseId") Long courseId);

}
