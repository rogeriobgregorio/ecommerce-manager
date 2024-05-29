package com.rogeriogregorio.ecommercemanager.services.strategy.validations.order;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.OrderStrategy;
import org.springframework.stereotype.Component;

@Component
public class ValidateEmailEnable implements OrderStrategy {

    @Override
    public void validateOrder(Order order) {

        boolean isEmailEnabled = order.getClient().isEmailEnabled();

        if (!isEmailEnabled) {
            throw new IllegalStateException("Unable to process payment: email not enabled.");
        }
    }
}
