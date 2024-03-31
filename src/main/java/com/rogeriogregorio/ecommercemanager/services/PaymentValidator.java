package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import org.springframework.stereotype.Component;

@Component
public interface PaymentValidator {
    void validate(Order order);
}