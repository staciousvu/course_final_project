package com.example.courseapplicationproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.courseapplicationproject.entity.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {}
