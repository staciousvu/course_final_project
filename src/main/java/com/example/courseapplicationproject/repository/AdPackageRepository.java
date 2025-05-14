package com.example.courseapplicationproject.repository;

import com.example.courseapplicationproject.entity.AdPackage;
import com.example.courseapplicationproject.entity.Cart;
import com.example.courseapplicationproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdPackageRepository extends JpaRepository<AdPackage, Long> {

}
