package com.rogeriogregorio.ecommercemanager.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplateImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class TokenServiceImpl extends ErrorHandlerTemplateImpl implements TokenService {

    @Value("${api.security.token.secret}")
    private String secretKey;

    public String generateToken(User user) {

            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            return handleError(() -> JWT
                    .create()
                    .withIssuer("ecommerce-manager")
                    .withSubject(user.getEmail())
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm), "Error generating token");
    }

    public String validateToken(String token) {

            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            return handleError(() -> JWT
                    .require(algorithm)
                    .withIssuer("ecommerce-manager")
                    .build()
                    .verify(token)
                    .getSubject(), "Error validating token");
    }

    private Instant generateExpirationDate() {

        return Instant.now().plus(2, ChronoUnit.HOURS);
    }
}