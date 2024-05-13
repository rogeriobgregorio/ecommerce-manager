package com.rogeriogregorio.ecommercemanager.services.strategy.payment;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.strategy.PaymentStrategy;
import org.springframework.stereotype.Component;

@Component
public class DeliveryAddressPresentStrategyImpl implements PaymentStrategy {

    @Override
    public void validateOrder(Order order) {

        boolean isDeliveryAddressNull = order.getClient().isAddressNull();

        if (isDeliveryAddressNull) {
            throw new IllegalStateException("Unable to process payment: delivery address missing.");
        }
    }
}