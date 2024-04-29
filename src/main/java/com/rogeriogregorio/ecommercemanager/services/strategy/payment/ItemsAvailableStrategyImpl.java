package com.rogeriogregorio.ecommercemanager.services.strategy.payment;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.InventoryItemService;
import com.rogeriogregorio.ecommercemanager.services.strategy.PaymentStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemsAvailableStrategyImpl implements PaymentStrategy {

    private final InventoryItemService inventoryItemService;

    @Autowired
    public ItemsAvailableStrategyImpl(InventoryItemService inventoryItemService) {
        this.inventoryItemService = inventoryItemService;
    }

    @Override
    public void validate(Order order) {

        inventoryItemService.isListItemsAvailable(order);
    }
}
