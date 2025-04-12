package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.CourseRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CourseRequirementRepository extends JpaRepository<CourseRequirement,Long>  {
}
