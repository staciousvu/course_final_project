package com.example.courseapplicationproject.repository;

import java.util.Collection;
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

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.subCategories")
    List<Category> findAllWithSubCategories();

    @Query("select c from Category c left join c.courses courses group by c order by COUNT(courses) DESC")
    List<Category> findAllSortedByCourseCountDESC();

    List<Category> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT c.id FROM Category c WHERE c.parentCategory.id = :rootCategoryId")
    List<Long> findSubCategoryIdsByRootCategory(@Param("rootCategoryId") Long rootCategoryId);

    @Query(
            """
		SELECT c.id FROM Category c
		WHERE c.id NOT IN (SELECT DISTINCT sc.parentCategory.id FROM Category sc WHERE sc.parentCategory IS NOT NULL)
		AND c.id IN :categoryIds
	""")
    List<Long> findLeafCategories(@Param("categoryIds") List<Long> categoryIds);

    @Query(
            value =
                    """
		WITH RECURSIVE category_tree AS (
			SELECT id FROM category WHERE id = :parentId
			UNION ALL
			SELECT c.id FROM category c
			INNER JOIN category_tree ct ON c.parent_id = ct.id
		)
		SELECT id FROM category_tree
	""",
            nativeQuery = true)
    List<Long> findAllSubCategoryIds(@Param("parentId") Long parentId);

    @Query(
            value = """
        WITH RECURSIVE category_tree AS (
            SELECT * FROM category WHERE id = :parentId
            UNION ALL
            SELECT c.* FROM category c
            INNER JOIN category_tree ct ON c.parent_id = ct.id
        )
        SELECT ct.*
        FROM category_tree ct
        LEFT JOIN category c ON c.parent_id = ct.id
        WHERE c.id IS NULL
        AND ct.id != :parentId
        """,
            nativeQuery = true)
    List<Category> findAllTopicCategoryIds(@Param("parentId") Long parentId);

    @Query("SELECT c FROM Category c WHERE c.parentCategory IS NULL")
    List<Category> findRootCategories();

    boolean existsByParentCategory(Category parentCategory);

    List<Category> findByParentCategoryIsNullAndIsActiveTrue();

    List<Category> findByParentCategoryId(Long parentId);

    @Query("SELECT c FROM Category c WHERE c.id NOT IN (" +
            "SELECT DISTINCT sc.parentCategory.id FROM Category sc WHERE sc.parentCategory IS NOT NULL)")
    List<Category> findAllLeafCategories();

    @Query("select count(c) from Course c where c.category.id=:topicId")
    Long countCourseByTopicId(@Param("topicId") Long topicId);
}
