package com.adp.interview.discountapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.*;

import com.adp.interview.discountapi.entity.*;
import com.adp.interview.discountapi.service.impl.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
public class DiscountControllerTest {

    @Mock
    private DiscountServiceImpl discountService;

    @InjectMocks
    private DiscountController discountController;

    private List<Discount> discounts;

    @BeforeEach
    void setUp() {
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
    void testGetAllDiscounts() {
        // set up mock objects
        when(discountService.getAllDiscounts()).thenReturn(discounts);

        // invoke the method to be tested
        List<Discount> result = discountController.getAllDiscounts();

        // check the results
        assertEquals(discounts, result);
        verify(discountService, times(1)).getAllDiscounts();
    }

    @Test
    void testGetDiscountByCode() {
        // create test data
        // set up mock objects
        when(discountService.getDiscountByCode("DISCOUNT1")).thenReturn(Optional.of(discounts.get(0)));

        // invoke the method to be tested
        ResponseEntity<Discount> result = discountController.getDiscountByCode("DISCOUNT1");

        // check the results
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(discounts.get(0), result.getBody());
        verify(discountService, times(1)).getDiscountByCode("DISCOUNT1");
    }

    @Test
    void testGetDiscountByCode_NotFound() {
        // set up mock objects
        when(discountService.getDiscountByCode("code1")).thenReturn(Optional.empty());

        // invoke the method to be tested
        ResponseEntity<Discount> result = discountController.getDiscountByCode("code1");

        // check the results
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(discountService, times(1)).getDiscountByCode("code1");
    }

    @Test
    void testAddDiscount() {
        // create test data
        Discount discount4 = new Discount();
        discount4.setDiscountCode("DISCOUNT4");
        discount4.setDiscountPercentage(10);
        discount4.setDiscountType(DiscountType.ITEM_TYPE);
        discount4.setApplicableItemType(ItemType.CLOTHING);
        discounts.add(discount4);

        // set up mock objects
        when(discountService.addDiscount(discount4)).thenReturn(discount4);

        // invoke the method to be tested
        ResponseEntity<Discount> result = discountController.addDiscount(discount4);

        // check the results
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(discount4, result.getBody());
        verify(discountService, times(1)).addDiscount(discount4);
    }

    @Test
    void testAddDiscountBadRequest() {
        // create test data
        Discount discount4 = new Discount();
        discount4.setDiscountCode("DISCOUNT4");
        discount4.setDiscountPercentage(10);
        discount4.setDiscountType(DiscountType.ITEM_TYPE);
        discount4.setApplicableItemType(ItemType.CLOTHING);
        discounts.add(discount4);

        // set up mock objects
        when(discountService.addDiscount(discount4)).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad Request"));

        // invoke the method to be tested
        assertThrows(ResponseStatusException.class, () -> discountController.addDiscount(discount4));

        // check the results
        verify(discountService, times(1)).addDiscount(discount4);
    }

    @Test
    void testDeleteDiscount() {
        // invoke the method to be tested
        ResponseEntity<Void> result = discountController.deleteDiscount("code1");

        // check the results
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(discountService, times(1)).deleteDiscount("code1");
    }

    @Test
    void testCalculateBestDiscount() {
        // create test data
        List<Item> items = new ArrayList<>();
        Item item1 = new Item("1", 100.0, ItemType.CLOTHING, 2);
        items.add(item1);
        Item item2 = new Item("2", 100.0, ItemType.ELECTRONICS, 2);
        items.add(item2);

        DiscountRequest request = new DiscountRequest();
        request.setItems(items);

        DiscountResponse expectedResponse = new DiscountResponse();
        expectedResponse.setTotalCost(270.0);
        expectedResponse.setDiscountCode("ABC");
        expectedResponse.setTotalDiscount(12);
        expectedResponse.setTotalCostAfterDiscount(34);

        // set up mock objects
        when(discountService.calculateBestDiscount(request)).thenReturn(expectedResponse);

        // invoke the method to be tested
        ResponseEntity<DiscountResponse> result = discountController.calculateBestDiscount(request);

        // check the results
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());
        verify(discountService, times(1)).calculateBestDiscount(request);
    }

    @Test
    void testCalculateBestDiscount_NoItems() {
        // create test data
        List<Item> items = new ArrayList<>();
        DiscountRequest request = new DiscountRequest();
        request.setItems(items);

        DiscountResponse expectedResponse = new DiscountResponse();
        expectedResponse.setTotalCost(270.0);
        expectedResponse.setDiscountCode("ABC");
        expectedResponse.setTotalDiscount(12);
        expectedResponse.setTotalCostAfterDiscount(34);

        // set up mock objects
        when(discountService.calculateBestDiscount(request)).thenReturn(expectedResponse);

        // invoke the method to be tested
        ResponseEntity<DiscountResponse> result = discountController.calculateBestDiscount(request);

        // check the results
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());
        verify(discountService, times(1)).calculateBestDiscount(request);
    }

    @Test
    void testCalculateBestDiscount_NoDiscount() {
        List<Item> items = new ArrayList<>();
        Item item1 = new Item("1", 100.0, ItemType.CLOTHING, 2);
        items.add(item1);
        Item item2 = new Item("2", 100.0, ItemType.ELECTRONICS, 2);
        items.add(item2);


        DiscountRequest request = new DiscountRequest();
        request.setItems(items);


        DiscountResponse expectedResponse = new DiscountResponse();
        expectedResponse.setTotalCost(270.0);
        expectedResponse.setDiscountCode("ABC");
        expectedResponse.setTotalDiscount(12);
        expectedResponse.setTotalCostAfterDiscount(34);

        // set up mock objects
        when(discountService.calculateBestDiscount(request)).thenReturn(expectedResponse);

        // invoke the method to be tested
        ResponseEntity<DiscountResponse> result = discountController.calculateBestDiscount(request);

        // check the results
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());
        verify(discountService, times(1)).calculateBestDiscount(request);
    }
}