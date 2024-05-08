package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.entities.User;
import org.springframework.stereotype.Component;

@Component
public interface MailService {

    void sendAccountActivationEmail(User user);

    String generateEmailVerificationToken(User user);

    void validateEmailVerificationToken(String token);
}
