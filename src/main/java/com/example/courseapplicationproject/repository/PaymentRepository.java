package com.example.courseapplicationproject.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("select p from Payment p where p.transactionId =:transactionId")
    Optional<Payment> findByTransactionId(@Param("transactionId") String transactionId);

    @Query("SELECT COALESCE(SUM(pd.price), 0) FROM PaymentDetails pd " +
            "JOIN pd.payment p JOIN pd.course c " +
            "WHERE c.id = :courseId AND p.paymentStatus = 'SUCCESS'")
    BigDecimal revenueByCourse(@Param("courseId") Long courseId);

    List<Payment> findAllByPaymentStatusAndExpiredTimeBefore(Payment.PaymentStatus paymentStatus, LocalDateTime now);
}
