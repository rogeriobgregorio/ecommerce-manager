package com.rogeriogregorio.ecommercemanager.services.strategy.payment;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.strategy.PaymentStrategy;

public class VerifiedEmailStrategyImpl implements PaymentStrategy {

    @Override
    public void validate(Order order) {

        boolean isEmailEnabled = order.getClient().isEmailEnabled();

        if (!isEmailEnabled) {
            throw new IllegalStateException("Unable to process payment: email not enabled.");
        }
    }
}
