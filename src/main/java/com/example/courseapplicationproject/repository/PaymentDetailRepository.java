package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentDetailRepository extends JpaRepository<PaymentDetails,Long> {
}
