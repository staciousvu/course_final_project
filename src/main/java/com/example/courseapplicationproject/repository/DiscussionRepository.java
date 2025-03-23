package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.Discussion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
    Page<Discussion> findByLectureIdAndContentContainingIgnoreCase(Long lectureId, String keyword, Pageable pageable);

    Page<Discussion> findByLectureId(Long lectureId, Pageable pageable);

    Page<Discussion> findByContentContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("""
    SELECT d FROM Discussion d
    WHERE d.lecture.id IN (SELECT l.id FROM Lecture l WHERE l.section.course.id = :courseId)
    AND LOWER(d.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
""")
    Page<Discussion> findByCourseIdAndContentContainingIgnoreCase(
            @Param("courseId") Long courseId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
    @Query("""
    SELECT d FROM Discussion d
    WHERE d.lecture.id IN (SELECT l.id FROM Lecture l WHERE l.section.course.id = :courseId)
""")
    Page<Discussion> findByCourseId(
            @Param("courseId") Long courseId,
            Pageable pageable
    );

}
