package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.Cart;
import com.example.courseapplicationproject.entity.CourseReport;
import com.example.courseapplicationproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<CourseReport, Long> {

    List<CourseReport> findByStatusOrderByCreatedAtDesc(CourseReport.ReportStatus reportStatus);
}
