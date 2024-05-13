package com.rogeriogregorio.ecommercemanager.services.strategy.payment;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.strategy.PaymentStrategy;
import org.springframework.stereotype.Component;

@Component
public class OrderItemsPresentStrategyImpl implements PaymentStrategy {

    @Override
    public void validateOrder(Order order) {

        boolean isOrderItemsEmpty = order.getItems().isEmpty();

        if (isOrderItemsEmpty) {
            throw new IllegalStateException("Unable to process payment: no items in the order.");
        }
    }
}