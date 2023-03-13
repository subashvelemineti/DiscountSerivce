package com.adp.interview.discountapi.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Item {

    @NonNull
    private String id;

    @NonNull
    private Double cost;

    @NonNull
    private ItemType type;

    @NonNull
    private Integer quantity;

}