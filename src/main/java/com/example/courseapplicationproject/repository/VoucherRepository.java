package com.example.courseapplicationproject.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.courseapplicationproject.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher,Long> {
    boolean existsByCode(String code);

    Optional<Voucher> findByCode(String code);

    List<Voucher> findByIsActiveTrue();
    List<Voucher> findByIsActiveFalse();
}
