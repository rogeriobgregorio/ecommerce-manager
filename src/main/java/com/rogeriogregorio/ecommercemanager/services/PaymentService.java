package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.Payment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PaymentService {

    List<PaymentResponse> findAllPayments();

    PaymentResponse createPayment(PaymentRequest paymentRequest);

    PaymentResponse findPaymentResponseById(Long id);

    Payment findPaymentById(Long id);

    void deletePayment(Long id);

    Payment buildPayment(PaymentRequest paymentRequest);

    void validatePayment(Order order);
}
