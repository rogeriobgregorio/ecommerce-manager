package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.PixListChargeDto;
import com.rogeriogregorio.ecommercemanager.dto.PixWebhookDto;
import com.rogeriogregorio.ecommercemanager.payment.PixService;
import com.rogeriogregorio.ecommercemanager.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class PixController {

    private final PixService pixService;
    private final PaymentService paymentService;

    @Autowired
    public PixController(PixService pixService, PaymentService paymentService) {
        this.pixService = pixService;
        this.paymentService = paymentService;
    }

    @GetMapping("/pix/charges/search")
    public ResponseEntity<PixListChargeDto> getAllPixCharges(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pixService.listPixCharges(startDate, endDate));
    }

    @PostMapping("/webhook/pix")
    public ResponseEntity<Void> webhookPix(@RequestBody PixWebhookDto pixWebhook) {

        paymentService.savePaidPixCharges(pixWebhook);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody String webhook) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
