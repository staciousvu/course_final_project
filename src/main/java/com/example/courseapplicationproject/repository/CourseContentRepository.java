package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.CourseContent;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseContentRepository extends JpaRepository<CourseContent,Long> {
    List<CourseContent> findByCourseIdOrderByIdAsc(Long courseId);

}
