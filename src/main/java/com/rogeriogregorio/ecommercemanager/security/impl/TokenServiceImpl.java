package com.rogeriogregorio.ecommercemanager.security.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
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
    private static final String ISSUER_NAME = "ecommerce-manager";
    private final UserRepository userRepository;
    private final ErrorHandler errorHandler;

    @Autowired
    public TokenServiceImpl(UserRepository userRepository, ErrorHandler errorHandler) {
        this.userRepository = userRepository;
        this.errorHandler = errorHandler;
    }

    public String generateAuthenticationToken(User user) {

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return errorHandler.catchException(() -> JWT
                        .create()
                        .withIssuer(ISSUER_NAME)
                        .withSubject(user.getEmail())
                        .withExpiresAt(generateExpirationDate())
                        .sign(algorithm),
                "Error generating token");
    }

    public String validateToken(String token) {

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return errorHandler.catchException(() -> JWT
                        .require(algorithm)
                        .withIssuer(ISSUER_NAME)
                        .build()
                        .verify(token)
                        .getSubject(),
                "Error validating token");
    }

    private Instant generateExpirationDate() {
        return Instant.now().plus(2, ChronoUnit.HOURS);
    }

    public String generateEmailVerificationToken(User user) {

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return errorHandler.catchException(() -> JWT
                        .create()
                        .withIssuer(ISSUER_NAME)
                        .withSubject(user.getEmail())
                        .withClaim("userId", user.getId())
                        .withExpiresAt(generateExpirationDate())
                        .sign(algorithm),
                "Error generating email verification token");
    }

    public boolean verifyEmailVerificationToken(String token, User user) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);

            JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER_NAME).build();

            DecodedJWT decodedJWT = verifier.verify(token);

            // Verificar se o token contém o "userId" correto
            String userIdFromToken = decodedJWT.getClaim("userId").asString();

            if (!userIdFromToken.equals(user.getId().toString())) {
                // O "userId" no token não corresponde ao usuário
                return false;
            }

            // Verificar se o token não está expirado
            Instant expirationDate = decodedJWT.getExpiresAt().toInstant();

            // O token está expirado
            return !expirationDate.isBefore(Instant.now());

            // Token válido e informações correspondentes

        } catch (JWTVerificationException e) {
            // Erro ao verificar o token
            return false;
        }
    }

    public void markEmailAsVerified(User user) {
        user.setEnabled(true);
        // Atualize o usuário no banco de dados para refletir o email como verificado
        userRepository.save(user);
    }


}
