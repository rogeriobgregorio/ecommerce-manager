package com.rogeriogregorio.ecommercemanager.services.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.services.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenServiceImpl implements TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("ecommerce-manager")
                    .withSubject(user.getEmail())
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException jwtCreationException) {
            throw new RuntimeException("Error while generating token", jwtCreationException);
        }
    }

    public String validateToken(String token) {

        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("ecommerce-manager")
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException jwtVerificationException) {
            return "";
        }
    }

    private Instant generateExpirationDate() {

        return Instant.now().plus(2, ChronoUnit.HOURS);
    }
}
