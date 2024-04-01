package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.Payment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public interface PaymentService {

    Page<PaymentResponse> findAllPayments(int page, int size);

    PaymentResponse createPayment(PaymentRequest paymentRequest);

    PaymentResponse findPaymentResponseById(Long id);

    Payment findPaymentById(Long id);

    void deletePayment(Long id);
}
