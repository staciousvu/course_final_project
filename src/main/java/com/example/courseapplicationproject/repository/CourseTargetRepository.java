package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.CourseTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseTargetRepository extends JpaRepository<CourseTarget,Long> {
}
