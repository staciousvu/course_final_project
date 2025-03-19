package com.example.courseapplicationproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.courseapplicationproject.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);

    List<Category> findByIsActiveTrue();

    @Query("SELECT c FROM Category c WHERE c.parentCategory.id = :parentId")
    List<Category> findByParentCategory_Id(@Param("parentId") Long parentId);

    @Query("select c from Category c order by c.displayOrder asc")
    List<Category> findAllSortedByDisplayOrder();

    @Query("select c from Category c left join c.courses courses group by c order by COUNT(courses) DESC")
    List<Category> findAllSortedByCourseCountDESC();

    List<Category> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT c.id FROM Category c WHERE c.parentCategory.id = :rootCategoryId")
    List<Long> findSubCategoryIdsByRootCategory(@Param("rootCategoryId") Long rootCategoryId);

    @Query("""
        SELECT c.id FROM Category c
        WHERE c.id NOT IN (SELECT DISTINCT sc.parentCategory.id FROM Category sc WHERE sc.parentCategory IS NOT NULL)
        AND c.id IN :categoryIds
    """)
    List<Long> findLeafCategories(@Param("categoryIds") List<Long> categoryIds);

}
