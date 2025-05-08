package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.Slide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SlideRepository extends JpaRepository<Slide, Long> {

    List<Slide> findAllByOrderByPositionAsc();
}
