package com.example.courseapplicationproject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.Lecture;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
}
