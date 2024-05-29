package com.rogeriogregorio.ecommercemanager.services.strategy.validations.order;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.OrderStrategy;
import org.springframework.stereotype.Component;

@Component
public class ValidateOrderAddress implements OrderStrategy {

    @Override
    public void validateOrder(Order order) {

        boolean isDeliveryAddressNull = order.getClient().isAddressNull();

        if (isDeliveryAddressNull) {
            throw new IllegalStateException("Unable to process payment: delivery address missing.");
        }
    }
}