package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.entities.User;
import org.springframework.stereotype.Component;

@Component
public interface TokenService {

    String generateToken(User user);

    String validateToken(String token);
}
