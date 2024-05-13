package com.rogeriogregorio.ecommercemanager.services.strategy.payment;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.strategy.PaymentStrategy;
import org.springframework.stereotype.Component;

@Component
public class OrderPaidStrategyImpl implements PaymentStrategy {

    @Override
    public void validateOrder(Order order) {

        boolean isOrderPaid = order.isOrderPaid();

        if (isOrderPaid) {
            throw new IllegalStateException("Unable to process payment: the order is already paid.");
        }
    }
}