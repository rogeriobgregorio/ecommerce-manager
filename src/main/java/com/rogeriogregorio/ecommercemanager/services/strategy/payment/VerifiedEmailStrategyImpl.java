package com.rogeriogregorio.ecommercemanager.services.strategy.payment;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.strategy.PaymentStrategy;
import org.springframework.stereotype.Component;

@Component
public class VerifiedEmailStrategyImpl implements PaymentStrategy {

    @Override
    public void validateOrder(Order order) {

        boolean isEmailEnabled = order.getClient().isEmailEnabled();

        if (!isEmailEnabled) {
            throw new IllegalStateException("Unable to process payment: email not enabled.");
        }
    }
}
