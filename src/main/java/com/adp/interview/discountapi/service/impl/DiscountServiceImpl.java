package com.adp.interview.discountapi.service.impl;

import java.util.List;
import java.util.Optional;

import com.adp.interview.discountapi.entity.*;
import com.adp.interview.discountapi.repository.DiscountRepository;
import com.adp.interview.discountapi.service.DiscountService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;

    public DiscountServiceImpl(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    public Optional<Discount> getDiscountByCode(String discountCode) {
        return discountRepository.findById(discountCode);
    }

    public Discount addDiscount(Discount discount) {
        validateDiscount(discount);
        return discountRepository.save(discount);
    }

    public void deleteDiscount(String discountCode) {
        discountRepository.deleteById(discountCode);
    }

    public void validateDiscount(Discount discount) {
        switch (discount.getDiscountType()) {
            case ITEM_TYPE:
                if (discount.getApplicableItemType() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Applicable item type must be supplied with ITEM_TYPE discount.");
                }
                if (discount.getApplicableItemId() != null ||
                        discount.getItemQuantityThreshold() != 0 ||
                        discount.getMinimumCost() != 0.0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Applicable item id, item count and minimum cost should not be supplied with ITEM_TYPE discount.");
                }
                break;
            case ITEM_COUNT:
                if (discount.getApplicableItemId() == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Applicable item id must be supplied with ITEM_COUNT discount.");
                }
                if (discount.getItemQuantityThreshold() == 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item count must be greater than zero.");
                }
                if (discount.getMinimumCost() != 0.0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimum cost should not be supplied with ITEM_COUNT discount.");
                }
                break;
            case ITEM_COST:
                if (discount.getApplicableItemId() != null ||
                        discount.getItemQuantityThreshold() != 0 ||
                        discount.getMinimumCost() == 0.0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimum cost must be supplied with ITEM_COST discount.");
                }
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid discount type: " + discount.getDiscountType());
        }
    }

    @Override
    public DiscountResponse calculateBestDiscount(DiscountRequest request) {
        List<Item> items = request.getItems();
        List<Discount> discounts = discountRepository.findAll();
        double totalDiscount = 0.0;
        Discount bestDiscount = null;
        for (Discount discount : discounts) {
            double discountAmount = 0.0;
            if (discount.getDiscountType() == DiscountType.ITEM_TYPE) {
                for (Item item : items) {
                    if (item.getType().equals(discount.getApplicableItemType())) {
                        discountAmount += (item.getCost() * item.getQuantity() * discount.getDiscountPercentage()/100);
                    }
                }
            } else if (discount.getDiscountType() == DiscountType.ITEM_COUNT) {
                for (Item item : items) {
                    if (item.getId().equals(discount.getApplicableItemId())) {
                        if (item.getQuantity() >= discount.getItemQuantityThreshold()) {
                            discountAmount += (item.getQuantity() * item.getCost() * discount.getDiscountPercentage()/100);
                        }
                        break;
                    }
                }
            } else if (discount.getDiscountType() == DiscountType.ITEM_COST) {
                for (Item item : items) {
                    if (item.getCost() > discount.getMinimumCost()) {
                        discountAmount += (item.getCost() * item.getQuantity() * discount.getDiscountPercentage()/100);
                    }
                }
            }

            if (discountAmount > totalDiscount) {
                totalDiscount = discountAmount;
                bestDiscount = discount;
            }
        }
        System.out.println("Best discount: " + bestDiscount);
        double totalCost = items.stream().mapToDouble(item -> item.getCost() * item.getQuantity()).sum();
        DiscountResponse discountResponse = new DiscountResponse();
        if (bestDiscount != null) {
            discountResponse.setDiscountCode(bestDiscount.getDiscountCode());
        }
        discountResponse.setTotalCost(totalCost);
        discountResponse.setTotalDiscount(totalDiscount);
        discountResponse.setTotalCostAfterDiscount(totalCost - totalDiscount);

        return discountResponse;
    }
}