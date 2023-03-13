package com.adp.interview.discountapi.entity;

import lombok.Data;

@Data
public class DiscountResponse {
    private String discountCode;
    private double totalCost;
    private double totalDiscount;
    private double totalCostAfterDiscount;
}