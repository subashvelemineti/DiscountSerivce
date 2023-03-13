package com.adp.interview.discountapi.controller;

import com.adp.interview.discountapi.entity.Discount;
import com.adp.interview.discountapi.entity.DiscountRequest;
import com.adp.interview.discountapi.entity.DiscountResponse;
import com.adp.interview.discountapi.service.impl.DiscountServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/discounts")
public class DiscountController {
    private final DiscountServiceImpl discountService;

    public DiscountController(DiscountServiceImpl discountService) {
        this.discountService = discountService;
    }

    @GetMapping
    public List<Discount> getAllDiscounts() {
        return discountService.getAllDiscounts();
    }

    @GetMapping("/{discountCode}")
    public ResponseEntity<Discount> getDiscountByCode(@PathVariable String discountCode) {
        Optional<Discount> discount = discountService.getDiscountByCode(discountCode);
        return discount.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Discount> addDiscount(@Valid @RequestBody Discount discount) {
        Discount newDiscount = discountService.addDiscount(discount);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDiscount);
    }

    @DeleteMapping("/{discountCode}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable String discountCode) {
        discountService.deleteDiscount(discountCode);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/calculate")
    public ResponseEntity<DiscountResponse> calculateBestDiscount(@Valid @RequestBody DiscountRequest request) {
        DiscountResponse discountResponse = discountService.calculateBestDiscount(request);
        return ResponseEntity.ok(discountResponse);
    }
}
