package com.adp.interview.discountapi.repository;

import com.adp.interview.discountapi.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, String> {
    // You can define additional custom methods here if needed
}