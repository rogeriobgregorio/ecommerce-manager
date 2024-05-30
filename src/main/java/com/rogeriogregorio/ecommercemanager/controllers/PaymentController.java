package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping(value = "/payments")
    public ResponseEntity<List<PaymentResponse>> getAllPayments(Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paymentService.findAllPayments(pageable).getContent());
    }

    @PostMapping(value = "/payments")
    public ResponseEntity<PaymentResponse> postPayment(
            @Valid @RequestBody PaymentRequest paymentRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.createPaymentProcess(paymentRequest));
    }

    @GetMapping(value = "/payments/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paymentService.findPaymentById(id));
    }

    @DeleteMapping(value = "/payments/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {

        paymentService.deletePayment(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}