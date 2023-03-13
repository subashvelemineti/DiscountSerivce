package com.adp.interview.discountapi.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.adp.interview.discountapi.entity.Discount;
import com.adp.interview.discountapi.entity.DiscountRequest;
import com.adp.interview.discountapi.entity.DiscountResponse;
import com.adp.interview.discountapi.entity.DiscountType;
import com.adp.interview.discountapi.entity.Item;
import com.adp.interview.discountapi.entity.ItemType;
import com.adp.interview.discountapi.repository.DiscountRepository;


@ExtendWith(MockitoExtension.class)
public class DiscountServiceImplTest {

    @Mock
    private DiscountRepository discountRepository;

    @InjectMocks
    private DiscountServiceImpl discountServiceImpl;

    private List<Discount> discounts;

    @BeforeEach
    public void setup() {
        discounts = new ArrayList<>();
        Discount discount1 = new Discount();
        discount1.setDiscountCode("DISCOUNT1");
        discount1.setDiscountPercentage(10);
        discount1.setDiscountType(DiscountType.ITEM_TYPE);
        discount1.setApplicableItemType(ItemType.CLOTHING);
        discounts.add(discount1);
        Discount discount2 = new Discount();
        discount2.setDiscountCode("DISCOUNT2");
        discount2.setDiscountPercentage(20);
        discount2.setDiscountType(DiscountType.ITEM_COUNT);
        discount2.setApplicableItemId("123");
        discount2.setItemQuantityThreshold(5);
        discounts.add(discount2);
        Discount discount3 = new Discount();
        discount3.setDiscountCode("DISCOUNT3");
        discount3.setDiscountPercentage(15);
        discount3.setDiscountType(DiscountType.ITEM_COST);
        discount3.setMinimumCost(50.0);
        discounts.add(discount3);
    }

    @Test
    public void testGetAllDiscounts() {
        when(discountRepository.findAll()).thenReturn(discounts);
        List<Discount> actualDiscounts = discountServiceImpl.getAllDiscounts();
        assertEquals(discounts, actualDiscounts);
    }

    @Test
    public void testGetDiscountByCode() {
        Discount expectedDiscount = discounts.get(0);
        when(discountRepository.findById(expectedDiscount.getDiscountCode())).thenReturn(Optional.of(expectedDiscount));
        Optional<Discount> actualDiscountOptional = discountServiceImpl.getDiscountByCode(expectedDiscount.getDiscountCode());
        assertEquals(expectedDiscount, actualDiscountOptional.get());
    }

    @Test
    public void testAddDiscount() {
        Discount expectedDiscount = new Discount();
        expectedDiscount.setDiscountCode("DISCOUNT4");
        expectedDiscount.setDiscountPercentage(25);
        expectedDiscount.setDiscountType(DiscountType.ITEM_COST);
        expectedDiscount.setMinimumCost(100.0);
        when(discountRepository.save(expectedDiscount)).thenReturn(expectedDiscount);
        Discount actualDiscount = discountServiceImpl.addDiscount(expectedDiscount);
        assertEquals(expectedDiscount, actualDiscount);
    }

    @Test
    public void testDeleteDiscount() {
        Discount expectedDiscount = discounts.get(0);
        discountServiceImpl.deleteDiscount(expectedDiscount.getDiscountCode());
    }

    @Test
    public void testValidateDiscountItemType_success() {
        Discount discount = new Discount();
        discount.setDiscountCode("DISCOUNT5");
        discount.setDiscountPercentage(10);
        discount.setDiscountType(DiscountType.ITEM_TYPE);
        discount.setApplicableItemType(ItemType.CLOTHING);
        discountServiceImpl.validateDiscount(discount);
    }

    @Test
    public void testValidateDiscountItemType_failure() {
        Discount discount = new Discount();
        discount.setDiscountCode("DISCOUNT5");
        discount.setDiscountPercentage(10);
        discount.setDiscountType(DiscountType.ITEM_TYPE);
        assertThrows(ResponseStatusException.class, () -> discountServiceImpl.validateDiscount(discount));
    }

    @Test
    public void testValidateDiscountItemCount_success() {
        Discount discount = new Discount();
        discount.setDiscountCode("DISCOUNT5");
        discount.setDiscountPercentage(10);
        discount.setDiscountType(DiscountType.ITEM_COUNT);
        discount.setApplicableItemId("123");
        discount.setItemQuantityThreshold(5);
        discountServiceImpl.validateDiscount(discount);
    }

    @Test
    public void testValidateDiscountItemCount_failure() {
        Discount discount = new Discount();
        discount.setDiscountCode("DISCOUNT5");
        discount.setDiscountPercentage(10);
        discount.setDiscountType(DiscountType.ITEM_COUNT);
        discount.setItemQuantityThreshold(5);
        assertThrows(ResponseStatusException.class, () -> discountServiceImpl.validateDiscount(discount));
    }

    @Test
    public void testValidateDiscountItemCost_success() {
        Discount discount = new Discount();
        discount.setDiscountCode("DISCOUNT5");
        discount.setDiscountPercentage(10);
        discount.setDiscountType(DiscountType.ITEM_COST);
        discount.setMinimumCost(50.0);
        discountServiceImpl.validateDiscount(discount);
    }

    @Test
    public void testValidateDiscountItemCost_failure() {
        Discount discount = new Discount();
        discount.setDiscountCode("DISCOUNT5");
        discount.setDiscountPercentage(10);
        discount.setDiscountType(DiscountType.ITEM_COST);
        assertThrows(ResponseStatusException.class, () -> discountServiceImpl.validateDiscount(discount));
    }

    @Test
    public void testCalculateBestDiscountForItemType() {
        // create test data
        List<Item> items = new ArrayList<>();
        items.add(new Item("id1",  100.0,  ItemType.ELECTRONICS, 2));
        items.add(new Item("id2",  50.0,  ItemType.CLOTHING,1));

        Discount discount1 = new Discount();
        discount1.setDiscountCode("DISCOUNT1");
        discount1.setDiscountPercentage(20);
        discount1.setDiscountType(DiscountType.ITEM_TYPE);
        discount1.setApplicableItemType(ItemType.ELECTRONICS);

        Discount discount2 = new Discount();
        discount2.setDiscountCode("DISCOUNT2");
        discount2.setDiscountPercentage(10);
        discount2.setDiscountType(DiscountType.ITEM_TYPE);
        discount2.setApplicableItemType(ItemType.CLOTHING);

        List<Discount> discounts = new ArrayList<>();
        discounts.add(discount1);
        discounts.add(discount2);

        DiscountRequest request = new DiscountRequest();
        request.setItems(items);

        // set up mock objects
        when(discountRepository.findAll()).thenReturn(discounts);

        // invoke the method to be tested
        DiscountResponse response = discountServiceImpl.calculateBestDiscount(request);

        // check the results
        assertNotNull(response);
        assertEquals(2 * 100.0 * 0.2, response.getTotalDiscount());
        assertEquals(2 * 100.0 + 50.0, response.getTotalCost());
        assertEquals(2 * 100.0 + 50.0 - 2 * 100.0 * 0.2 , response.getTotalCostAfterDiscount());
        assertEquals(discount1.getDiscountCode(), response.getDiscountCode());
    }

    @Test
    public void testCalculateBestDiscountForItemCount() {
        // create test data
        List<Item> items = new ArrayList<>();
        items.add(new Item("id1",  150.0,  ItemType.ELECTRONICS, 2));
        items.add(new Item("id2",  50.0,  ItemType.CLOTHING,1));

        Discount discount1 = new Discount();
        discount1.setDiscountCode("DISCOUNT1");
        discount1.setDiscountPercentage(30);
        discount1.setDiscountType(DiscountType.ITEM_COUNT);
        discount1.setApplicableItemId("id1");
        discount1.setItemQuantityThreshold(2);

        Discount discount2 = new Discount();
        discount2.setDiscountCode("DISCOUNT2");
        discount2.setDiscountPercentage(10);
        discount2.setDiscountType(DiscountType.ITEM_COUNT);
        discount2.setApplicableItemId("id2");
        discount2.setItemQuantityThreshold(2);


        List<Discount> discounts = new ArrayList<>();
        discounts.add(discount1);
        discounts.add(discount2);

        DiscountRequest request = new DiscountRequest();
        request.setItems(items);

        // set up mock objects
        when(discountRepository.findAll()).thenReturn(discounts);

        // invoke the method to be tested
        DiscountResponse response = discountServiceImpl.calculateBestDiscount(request);

        // check the results
        assertNotNull(response);
        assertEquals(2 * 150.0 * 0.3, response.getTotalDiscount());
        assertEquals(2 * 150.0 + 50.0, response.getTotalCost());
        assertEquals(2 * 150.0 + 50.0 - 2 * 150.0 * 0.3 , response.getTotalCostAfterDiscount());
        assertEquals(discount1.getDiscountCode(), response.getDiscountCode());
    }

    @Test
    public void testCalculateBestDiscountForItemCost() {
        // create test data
        List<Item> items = new ArrayList<>();
        items.add(new Item("id1",  150.0,  ItemType.ELECTRONICS, 2));
        items.add(new Item("id2",  50.0,  ItemType.CLOTHING,1));

        Discount discount1 = new Discount();
        discount1.setDiscountCode("DISCOUNT1");
        discount1.setDiscountPercentage(30);
        discount1.setDiscountType(DiscountType.ITEM_COST);
        discount1.setMinimumCost(100);

        Discount discount2 = new Discount();
        discount2.setDiscountCode("DISCOUNT2");
        discount2.setDiscountPercentage(10);
        discount2.setDiscountType(DiscountType.ITEM_COST);
        discount2.setMinimumCost(100);


        List<Discount> discounts = new ArrayList<>();
        discounts.add(discount1);
        discounts.add(discount2);

        DiscountRequest request = new DiscountRequest();
        request.setItems(items);

        // set up mock objects
        when(discountRepository.findAll()).thenReturn(discounts);

        // invoke the method to be tested
        DiscountResponse response = discountServiceImpl.calculateBestDiscount(request);

        // check the results
        assertNotNull(response);
        assertEquals(2 * 150.0 * 0.3, response.getTotalDiscount());
        assertEquals(2 * 150.0 + 50.0, response.getTotalCost());
        assertEquals(2 * 150.0 + 50.0 - 2 * 150.0 * 0.3 , response.getTotalCostAfterDiscount());
        assertEquals(discount1.getDiscountCode(), response.getDiscountCode());
    }

    @Test
    public void testCalculateBestDiscountForExample1() {
        // create test data
        List<Item> items = new ArrayList<>();
        items.add(new Item("123",  50.0,  ItemType.CLOTHING,1));

        Discount discount1 = new Discount();
        discount1.setDiscountCode("ABC");
        discount1.setDiscountPercentage(10);
        discount1.setDiscountType(DiscountType.ITEM_TYPE);
        discount1.setApplicableItemType(ItemType.CLOTHING);

        Discount discount2 = new Discount();
        discount2.setDiscountCode("CDE");
        discount2.setDiscountPercentage(15);
        discount2.setDiscountType(DiscountType.ITEM_COST);
        discount2.setMinimumCost(100);


        List<Discount> discounts = new ArrayList<>();
        discounts.add(discount1);
        discounts.add(discount2);

        DiscountRequest request = new DiscountRequest();
        request.setItems(items);

        // set up mock objects
        when(discountRepository.findAll()).thenReturn(discounts);

        // invoke the method to be tested
        DiscountResponse response = discountServiceImpl.calculateBestDiscount(request);

        // check the results
        assertNotNull(response);
        assertEquals(1 * 50.0 * 0.1, response.getTotalDiscount());
        assertEquals(1 * 50.0, response.getTotalCost());
        assertEquals(1 * 50.0 - 1 * 50.0 * 0.1 , response.getTotalCostAfterDiscount());
        assertEquals(discount1.getDiscountCode(), response.getDiscountCode());
    }


    @Test
    public void testCalculateBestDiscountForExample2() {
        // create test data
        List<Item> items = new ArrayList<>();
        items.add(new Item("123",  50.0,  ItemType.CLOTHING,5));

        Discount discount1 = new Discount();
        discount1.setDiscountCode("ABC");
        discount1.setDiscountPercentage(10);
        discount1.setDiscountType(DiscountType.ITEM_TYPE);
        discount1.setApplicableItemType(ItemType.CLOTHING);

        Discount discount2 = new Discount();
        discount2.setDiscountCode("CDE");
        discount2.setDiscountPercentage(15);
        discount2.setDiscountType(DiscountType.ITEM_COST);
        discount2.setMinimumCost(100);

        Discount discount3 = new Discount();
        discount3.setDiscountCode("FGH");
        discount3.setDiscountPercentage(20);
        discount3.setDiscountType(DiscountType.ITEM_COUNT);
        discount3.setItemQuantityThreshold(2);
        discount3.setApplicableItemId("123");

        List<Discount> discounts = new ArrayList<>();
        discounts.add(discount1);
        discounts.add(discount2);
        discounts.add(discount3);

        DiscountRequest request = new DiscountRequest();
        request.setItems(items);

        // set up mock objects
        when(discountRepository.findAll()).thenReturn(discounts);

        // invoke the method to be tested
        DiscountResponse response = discountServiceImpl.calculateBestDiscount(request);

        // check the results
        assertNotNull(response);
        assertEquals(5 * 50.0 * 0.2, response.getTotalDiscount());
        assertEquals(5 * 50.0, response.getTotalCost());
        assertEquals(5 * 50.0 - 5 * 50.0 * 0.2 , response.getTotalCostAfterDiscount());
        assertEquals(discount3.getDiscountCode(), response.getDiscountCode());
    }

    @Test
    public void testCalculateBestDiscountForExample3() {
        // create test data
        List<Item> items = new ArrayList<>();
        items.add(new Item("123",  50.0,  ItemType.CLOTHING,1));
        items.add(new Item("456",  300.0,  ItemType.ELECTRONICS,1));

        Discount discount1 = new Discount();
        discount1.setDiscountCode("ABC");
        discount1.setDiscountPercentage(10);
        discount1.setDiscountType(DiscountType.ITEM_TYPE);
        discount1.setApplicableItemType(ItemType.CLOTHING);

        Discount discount2 = new Discount();
        discount2.setDiscountCode("CDE");
        discount2.setDiscountPercentage(15);
        discount2.setDiscountType(DiscountType.ITEM_COST);
        discount2.setMinimumCost(100);

        List<Discount> discounts = new ArrayList<>();
        discounts.add(discount1);
        discounts.add(discount2);

        DiscountRequest request = new DiscountRequest();
        request.setItems(items);

        // set up mock objects
        when(discountRepository.findAll()).thenReturn(discounts);

        // invoke the method to be tested
        DiscountResponse response = discountServiceImpl.calculateBestDiscount(request);

        // check the results
        assertNotNull(response);
        assertEquals(1 * 300.0 * 0.15, response.getTotalDiscount());
        assertEquals(1 * 300.0 + 1* 50.0, response.getTotalCost());
        assertEquals(1 * 300.0 + 1* 50.0- 1 * 300.0 * 0.15 , response.getTotalCostAfterDiscount());
        assertEquals(discount2.getDiscountCode(), response.getDiscountCode());
    }
}