package com.rogeriogregorio.ecommercemanager.services.strategy.validations.order;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.OrderStrategy;
import org.springframework.stereotype.Component;

@Component
public class ValidateOrderItems implements OrderStrategy {

    @Override
    public void validateOrder(Order order) {

        boolean isOrderItemsEmpty = order.getItems().isEmpty();

        if (isOrderItemsEmpty) {
            throw new IllegalStateException("Unable to process payment: no items in the order.");
        }
    }
}