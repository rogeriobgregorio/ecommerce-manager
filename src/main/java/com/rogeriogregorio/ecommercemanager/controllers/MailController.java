package com.rogeriogregorio.ecommercemanager.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.rogeriogregorio.ecommercemanager.dto.PasswordResetDto;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1")
public class MailController {

    private final MailService mailService;

    @Autowired
    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping(value = "/mail/validate/search")
    public ResponseEntity<UserResponse> getEmailVerificationToken(@RequestParam("token") String token) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(mailService.validateEmailVerificationToken(token));
    }

    @PostMapping(value = "/mail/password-reset")
    public ResponseEntity<Void> postRequestPasswordReset(
            @Validated(PasswordResetDto.View.Request.class)
            @JsonView(PasswordResetDto.View.Request.class)
            @RequestBody PasswordResetDto passwordResetDTO) {

        mailService.sendPasswordResetEmail(passwordResetDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PutMapping(value = "/mail/password-reset")
    public ResponseEntity<Void> putResetPassword(
            @Validated(PasswordResetDto.View.Reset.class)
            @JsonView(PasswordResetDto.View.Reset.class)
            @RequestBody PasswordResetDto passwordResetDTO) {

        mailService.validatePasswordResetToken(passwordResetDTO);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
