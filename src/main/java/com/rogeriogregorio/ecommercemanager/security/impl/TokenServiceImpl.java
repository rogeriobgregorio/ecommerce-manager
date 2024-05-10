package com.rogeriogregorio.ecommercemanager.security.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.security.TokenService;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${api.security.token.secret}")
    private String secretKey;
    private static final String ISSUER_NAME = "ecommerce-manager";
    private final ErrorHandler errorHandler;

    @Autowired
    public TokenServiceImpl(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    private Instant generateExpirationDate() {

        return Instant.now().plus(2, ChronoUnit.HOURS);
    }

    public String generateAuthenticationToken(User user) {

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return errorHandler.catchException(() -> JWT
                        .create()
                        .withIssuer(ISSUER_NAME)
                        .withSubject(user.getEmail())
                        .withExpiresAt(generateExpirationDate())
                        .sign(algorithm),
                "Error while trying to generate token");
    }

    public String validateToken(String token) {

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return errorHandler.catchException(() -> JWT
                        .require(algorithm)
                        .withIssuer(ISSUER_NAME)
                        .build()
                        .verify(token)
                        .getSubject(),
                "Error while trying to validate token");
    }
}
