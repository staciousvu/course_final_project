package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.AdsApply;
import com.example.courseapplicationproject.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdsApplyRepository extends JpaRepository<AdsApply, Long> {

    List<AdsApply> findByStatus(AdsApply.ApplicationStatus applicationStatus);

    Optional<AdsApply> findByAdvertisementId(Long id);

    @Query("""
    SELECT aa
    FROM AdsApply aa
    JOIN FETCH aa.advertisement ad
    JOIN FETCH aa.course c
    WHERE aa.status = 'APPROVED'
      AND ad.used = true
      AND ad.startDate <= CURRENT_TIMESTAMP
      AND ad.endDate >= CURRENT_TIMESTAMP
""")
    List<AdsApply> findApprovedAndActiveAdvertisements();

}
