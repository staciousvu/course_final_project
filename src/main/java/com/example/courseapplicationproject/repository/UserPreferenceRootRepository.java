package com.example.courseapplicationproject.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.UserPreferenceRoot;

@Repository
public interface UserPreferenceRootRepository extends JpaRepository<UserPreferenceRoot, Long> {
    void deleteByUserId(Long userId);

    Optional<UserPreferenceRoot> findByUserId(Long userId);
}
