package com.rogeriogregorio.ecommercemanager.security;

import com.rogeriogregorio.ecommercemanager.entities.User;
import org.springframework.stereotype.Component;

@Component
public interface TokenService {

    String generateAuthenticationToken(User user);

    String validateAuthenticationToken(String token);

    String generateEmailToken(User user);

    User validateEmailToken(String token);
}