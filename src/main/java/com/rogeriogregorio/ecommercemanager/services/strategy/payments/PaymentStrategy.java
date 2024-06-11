package com.rogeriogregorio.ecommercemanager.services.strategy.payments;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.Payment;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentType;
import org.springframework.stereotype.Component;

@Component
public interface PaymentStrategy {

    PaymentType getSupportedPaymentMethod();

    Payment createPayment(Order order);
}
