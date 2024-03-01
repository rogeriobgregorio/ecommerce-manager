package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PaymentService {

    public List<PaymentResponse> findAllPayments();

    public PaymentResponse createPayment(PaymentRequest paymentRequest);

    public PaymentResponse findPaymentById(Long id);

    public void deletePayment(Long id);
}
