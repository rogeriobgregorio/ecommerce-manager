package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.PaymentResponse;
import com.rogeriogregorio.ecommercemanager.services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping(value = "/payments")
    public ResponseEntity<Page<PaymentResponse>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paymentService.findAllPayments(page, size));
    }

    @PostMapping(value = "/payments")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest paymentRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.createPayment(paymentRequest));
    }

    @GetMapping(value = "/payments/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paymentService.findPaymentResponseById(id));
    }

    @DeleteMapping(value = "/payments/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {

        paymentService.deletePayment(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}