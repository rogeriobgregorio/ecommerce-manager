package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PaymentService {

    List<PaymentResponse> findAllPayments();

    PaymentResponse createPayment(PaymentRequest paymentRequest);

    PaymentResponse findPaymentById(Long id);

    void deletePayment(Long id);
}
