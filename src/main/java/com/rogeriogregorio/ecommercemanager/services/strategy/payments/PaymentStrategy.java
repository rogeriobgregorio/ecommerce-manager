package com.rogeriogregorio.ecommercemanager.services.strategy.payments;

import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentMethod;
import org.springframework.stereotype.Component;

@Component
public interface PaymentStrategy {

    PaymentResponse createPayment(PaymentRequest paymentRequest);

    PaymentMethod getSupportedPaymentMethod();
}
