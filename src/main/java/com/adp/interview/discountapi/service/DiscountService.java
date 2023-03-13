package com.adp.interview.discountapi.service;

import com.adp.interview.discountapi.entity.Discount;
import com.adp.interview.discountapi.entity.DiscountRequest;
import com.adp.interview.discountapi.entity.DiscountResponse;

import java.util.List;
import java.util.Optional;

public interface DiscountService {
    List<Discount> getAllDiscounts();
    Optional<Discount> getDiscountByCode(String discountCode);
    Discount addDiscount(Discount discount);
    void deleteDiscount(String discountCode);
    DiscountResponse calculateBestDiscount(DiscountRequest request);
}
