package com.adp.interview.discountapi.entity;



import java.util.List;

public class DiscountRequest {

    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}