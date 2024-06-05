package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.PixWebhookDto;
import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public interface PaymentService {

    Page<PaymentResponse> findAllPayments(Pageable pageable);

    PaymentResponse createPaymentProcess(PaymentRequest paymentRequest);

    void savePaidPixCharges(PixWebhookDto pixWebhook);

    PaymentResponse findPaymentById(Long id);

    void deletePayment(Long id);
}
