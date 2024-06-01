package com.rogeriogregorio.ecommercemanager.services.strategy.payments;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.Payment;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentType;
import org.springframework.stereotype.Component;

@Component
public interface PaymentStrategy {

    Payment createPayment(Order order);

    PaymentType getSupportedPaymentMethod();
}
