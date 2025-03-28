package com.example.courseapplicationproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.Section;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {}
