package com.rogeriogregorio.ecommercemanager.services.strategy.validations.order;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.OrderStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidateInventoryItems implements OrderStrategy {

    private final InventoryItemService inventoryItemService;

    @Autowired
    public ValidateInventoryItems(InventoryItemService inventoryItemService) {
        this.inventoryItemService = inventoryItemService;
    }

    @Override
    public void validateOrder(Order order) {

        inventoryItemService.validateItemListAvailability(order);
    }
}
