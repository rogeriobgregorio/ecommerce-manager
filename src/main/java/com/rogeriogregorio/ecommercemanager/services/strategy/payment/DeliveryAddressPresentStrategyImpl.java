package com.rogeriogregorio.ecommercemanager.services.strategy.payment;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.PaymentStrategy;
import org.springframework.stereotype.Component;

@Component
public class DeliveryAddressPresentStrategyImpl implements PaymentStrategy {

    @Override
    public void validate(Order order) {

        boolean isDeliveryAddressNull = order.getClient().isAddressNull();

        if (isDeliveryAddressNull) {
            throw new IllegalStateException("Unable to process payment: delivery address missing.");
        }
    }
}