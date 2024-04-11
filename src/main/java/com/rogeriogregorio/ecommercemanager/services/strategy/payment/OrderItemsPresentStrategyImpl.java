package com.rogeriogregorio.ecommercemanager.services.strategy.payment;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.PaymentStrategy;
import org.springframework.stereotype.Component;

@Component
public class OrderItemsPresentStrategyImpl implements PaymentStrategy {

    @Override
    public void validate(Order order) {

        boolean isOrderItemsEmpty = order.getItems().isEmpty();

        if (isOrderItemsEmpty) {
            throw new IllegalStateException("Unable to process payment: no items in the order.");
        }
    }
}