package com.rogeriogregorio.ecommercemanager.services.strategy;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import org.springframework.stereotype.Component;

@Component
public interface PaymentStrategy {

    void validateOrder(Order order);
}
