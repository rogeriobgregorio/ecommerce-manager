package com.rogeriogregorio.ecommercemanager.security;

import com.rogeriogregorio.ecommercemanager.entities.User;
import org.springframework.stereotype.Component;

@Component
public interface TokenService {

    String generateAuthenticationToken(User user);

    String generateEmailVerificationToken(User user);

    String validateToken(String token);
}