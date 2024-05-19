package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.pix.PixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1")
public class PixController {

    private final PixService pixService;

    @Autowired
    public PixController(PixService pixService) {
        this.pixService = pixService;
    }

    @GetMapping("/pix/paid-charges")
    public ResponseEntity<String> getAllPaidPixCharges(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {

         return ResponseEntity
                 .status(HttpStatus.OK)
                 .body(pixService.listPaidPixCharges(startDate, endDate));
    }
}
