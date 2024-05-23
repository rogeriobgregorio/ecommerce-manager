package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.pix.PixService;
import com.rogeriogregorio.ecommercemanager.services.PaymentService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1")
public class PixController {

    private final PixService pixService;
    private final PaymentService paymentService;

    @Autowired
    public PixController(PixService pixService, PaymentService paymentService) {
        this.pixService = pixService;
        this.paymentService = paymentService;
    }

    @GetMapping("/pix/paid-charges/search")
    public ResponseEntity<String> getAllPaidPixCharges(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pixService.listPaidPixCharges(startDate, endDate));
    }

    @PostMapping("/webhook/pix")
    public ResponseEntity<Void> webhookPix(@RequestBody String pixWebhook) {

        paymentService.savePaidPayment(pixWebhook);

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
