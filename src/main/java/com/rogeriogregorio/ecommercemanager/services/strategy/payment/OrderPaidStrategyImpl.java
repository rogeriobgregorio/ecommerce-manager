package com.rogeriogregorio.ecommercemanager.services.strategy.payment;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.PaymentStrategy;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class OrderPaidStrategyImpl implements PaymentStrategy {

    @Override
    public void validate(Order order) {

        String orderStatus = order.getOrderStatus().name();
        boolean isOrderPaid = Set.of("PAID", "SHIPPED", "DELIVERED").contains(orderStatus);

        if (isOrderPaid) {
            throw new IllegalStateException("Unable to process payment: the order is already paid.");
        }
    }
}