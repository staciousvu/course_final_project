package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    List<Advertisement> findByUserIdAndUsedFalse(Long userId);

    List<Advertisement> findByUserIdAndUsedTrueAndEndDateAfter(Long id, LocalDateTime now);
}
