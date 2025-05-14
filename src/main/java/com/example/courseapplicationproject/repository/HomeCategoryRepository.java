package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.Favorite;
import com.example.courseapplicationproject.entity.HomeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeCategoryRepository extends JpaRepository<HomeCategory, Long> {
    void deleteByCategoryId(Long categoryId);
}
