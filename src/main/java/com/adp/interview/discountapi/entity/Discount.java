package com.adp.interview.discountapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

@Entity
@Data
public class Discount {

    @Id
    @NotBlank(message = "Discount code is required")
    private String discountCode; // unique identifier for a discount code

    @NotBlank(message = "Description is required")
    private String description; // description of the discount

    @Min(value = 0, message = "Discount percentage must be greater than or equal to 0")
    @Max(value = 100, message = "Discount percentage must be less than or equal to 100")
    private double discountPercentage; // discount percentage

    @NotNull(message = "Discount type is required")
    @Enumerated(EnumType.STRING)
    private DiscountType discountType; // type of discount

    @Enumerated(EnumType.STRING)
    private ItemType applicableItemType; // type of applicableItemType

    private String applicableItemId; // item id to which this discount applies

    private int itemQuantityThreshold; // minimum quantity of items needed to apply this discount

    private double minimumCost; // minimum cost of items needed to apply this discount

}