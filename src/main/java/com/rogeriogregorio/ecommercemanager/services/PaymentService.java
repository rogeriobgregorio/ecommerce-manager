package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public interface PaymentService {

    Page<PaymentResponse> findAllPayments(Pageable pageable);

    PaymentResponse createPayment(PaymentRequest paymentRequest);

    void savePaidPaymentsFromWebHook(JSONObject pixWebHook);

    PaymentResponse findPaymentById(Long id);

    void deletePayment(Long id);
}
