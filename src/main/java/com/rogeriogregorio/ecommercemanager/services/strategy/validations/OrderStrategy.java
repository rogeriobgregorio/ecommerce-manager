package com.rogeriogregorio.ecommercemanager.services.strategy.validations;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import org.springframework.stereotype.Component;

@Component
public interface OrderStrategy {

    void validateOrder(Order order);
}
