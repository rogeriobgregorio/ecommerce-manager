package com.rogeriogregorio.ecommercemanager.security.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.security.TokenService;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${api.security.token.secret}")
    private String secretKey;
    private final ErrorHandler errorHandler;

    @Autowired
    public TokenServiceImpl(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public String generateToken(User user) {

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return errorHandler.catchException(() -> JWT
                .create()
                .withIssuer("ecommerce-manager")
                .withSubject(user.getEmail())
                .withExpiresAt(generateExpirationDate())
                .sign(algorithm),
                "Error generating token");
    }

    public String validateToken(String token) {

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return errorHandler.catchException(() -> JWT
                .require(algorithm)
                .withIssuer("ecommerce-manager")
                .build()
                .verify(token)
                .getSubject(),
                "Error validating token");
    }

    private Instant generateExpirationDate() {

        return Instant.now().plus(2, ChronoUnit.HOURS);
    }
}