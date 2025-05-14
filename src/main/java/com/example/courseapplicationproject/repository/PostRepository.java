package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findBySlug(String slug);
    @Query("""
    SELECT p FROM Post p
    WHERE p.isPublished = true
    AND (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
""")
    Page<Post> findAll(@Param("keyword") String keyword, Pageable pageable);

}
