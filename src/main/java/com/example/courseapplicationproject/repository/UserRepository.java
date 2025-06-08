package com.example.courseapplicationproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("select count(distinct e.user.id) from Enrollment e where e.course.author.id = :authorId")
    int countStudentsByTeacherId(@Param("authorId") Long authorId);

    @Query("select count(distinct e.user.id) from Enrollment e join e.course c where c.author.id = :categoryId")
    int countStudentByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT u FROM User u JOIN u.roles r " +
            "WHERE r.roleName = 'ADMIN' " +
            "AND u.isEnabled = true " +
            "AND (:keyword IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> findAdmins(String keyword);

    @Query("SELECT u FROM User u JOIN u.roles r " +
            "WHERE r.roleName = 'LEARNER' " +
            "AND u.isEnabled = true " +
            "AND (:keyword IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> findLearners(String keyword);

    @Query("SELECT u FROM User u JOIN u.roles r " +
            "WHERE r.roleName = 'INSTRUCTOR' " +
//            "AND u.isEnabled = true " +
            "AND (:keyword IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> findInstructors(String keyword);



}
