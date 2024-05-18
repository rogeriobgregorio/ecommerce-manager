package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.pix.PixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1")
public class PixController {

    private final PixService pixService;

    @Autowired
    public PixController(PixService pixService) {
        this.pixService = pixService;
    }

    @GetMapping("/pix/evp")
    public ResponseEntity<String> getPixEVP() {

         return ResponseEntity
                 .status(HttpStatus.OK)
                 .body(pixService.createPixEVP());
    }

    @GetMapping("/pix/charge")
    public ResponseEntity<String> getImmediatePixCharge() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pixService.createImmediatePixCharge());
    }

    @GetMapping("/pix/qrcode")
    public ResponseEntity<String> getPixQRCode() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pixService.generatePixQRCodeLink());
    }
}
