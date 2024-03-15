package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.entities.PaymentEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PaymentService {

    List<PaymentResponse> findAllPayments();

    PaymentResponse createPayment(PaymentRequest paymentRequest);

    PaymentResponse findPaymentById(Long id);

    PaymentEntity findPaymentEntityById(Long id);

    void deletePayment(Long id);

    PaymentEntity buildPaymentFromRequest(PaymentRequest paymentRequest);
}
