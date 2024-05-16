package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.pix.AuthenticatePixService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1")
public class PixController {

    private final AuthenticatePixService authenticatePixService;

    @Autowired
    public PixController(AuthenticatePixService authenticatePixService) {
        this.authenticatePixService = authenticatePixService;
    }

    @GetMapping("/pix/authenticate")
    public String authenticate() {

        return authenticatePixService.generatePixAuthenticationToken();
    }
}
