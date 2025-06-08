package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.AdsApply;
import com.example.courseapplicationproject.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {

    List<Advertisement> findByUserIdAndUsedFalse(Long userId);

    @Query("""
    SELECT aa
    FROM Advertisement aa
    JOIN FETCH aa.course c
    WHERE aa.used = true
      AND aa.startDate <= CURRENT_TIMESTAMP
      AND aa.endDate >= CURRENT_TIMESTAMP
""")
    List<Advertisement> findActiveAdvertisements();

    List<Advertisement> findByUserIdAndUsedTrueAndEndDateAfter(Long id, LocalDateTime now);
}
