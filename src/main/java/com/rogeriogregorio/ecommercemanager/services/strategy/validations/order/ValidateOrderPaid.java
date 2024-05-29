package com.rogeriogregorio.ecommercemanager.services.strategy.validations.order;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.OrderStrategy;
import org.springframework.stereotype.Component;

@Component
public class ValidateOrderPaid implements OrderStrategy {

    @Override
    public void validateOrder(Order order) {

        boolean isOrderPaid = order.isOrderPaid();

        if (isOrderPaid) {
            throw new IllegalStateException("Unable to process payment: the order is already paid.");
        }
    }
}