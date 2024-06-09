package com.rogeriogregorio.ecommercemanager.security.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rogeriogregorio.ecommercemanager.dto.UserTokenDetailsDto;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.security.TokenService;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.TokenStrategy;
import com.rogeriogregorio.ecommercemanager.utils.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    private static final String ISSUER_NAME = "ecommerce-manager";
    private static final Instant EXPIRY_TIME = Instant.now().plus(2, ChronoUnit.HOURS);

    @Value("${api.security.token.secret}")
    private String secretKey;
    private final UserRepository userRepository;
    private final List<TokenStrategy> tokenValidators;
    private final ErrorHandler errorHandler;

    @Autowired
    public TokenServiceImpl(UserRepository userRepository,
                            List<TokenStrategy> tokenValidators,
                            ErrorHandler errorHandler) {

        this.userRepository = userRepository;
        this.tokenValidators = tokenValidators;
        this.errorHandler = errorHandler;
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secretKey);
    }

    public String generateAuthenticationToken(User user) {

        return errorHandler.catchException(
                () -> JWT.create()
                        .withIssuer(ISSUER_NAME)
                        .withSubject(user.getEmail())
                        .withExpiresAt(EXPIRY_TIME)
                        .sign(getAlgorithm()),
                "Error while trying to generate token"
        );
    }

    public String validateAuthenticationToken(String token) {

        return errorHandler.catchException(
                () -> JWT.require(getAlgorithm())
                        .withIssuer(ISSUER_NAME)
                        .build()
                        .verify(token)
                        .getSubject(),
                "Error while trying to validate token"
        );
    }

    public String generateEmailToken(User user) {

        return errorHandler.catchException(
                () -> JWT.create()
                        .withIssuer(ISSUER_NAME)
                        .withSubject(user.getEmail())
                        .withClaim("userId", String.valueOf(user.getId()))
                        .withClaim("userEmail", user.getEmail())
                        .withClaim("userPassword", user.getPassword())
                        .withExpiresAt(EXPIRY_TIME)
                        .sign(getAlgorithm()),
                "Error while trying to generate email token"
        );
    }

    public User validateEmailToken(String token) {

        DecodedJWT decodedJWT = errorHandler.catchException(
                () -> JWT.require(getAlgorithm())
                        .withIssuer(ISSUER_NAME)
                        .build()
                        .verify(token),
                "Error while trying to validate the token"
        );

        String userIdFromToken = decodedJWT.getClaim("userId").asString();
        User user = findUserByIdFromToken(userIdFromToken);

        UserTokenDetailsDto userTokenDetails = new UserTokenDetailsDto(user, decodedJWT);
        tokenValidators.forEach(strategy -> strategy.validateTokenClaim(userTokenDetails));

        return user;
    }

    @Transactional(readOnly = true)
    private User findUserByIdFromToken(String userIdFromToken) {

        return errorHandler.catchException(
                () -> userRepository.findById(UUID.fromString(userIdFromToken))
                        .orElseThrow(() -> new NotFoundException("The user with the token ID was not found")),
                "Error while trying to search for user by token ID: " + userIdFromToken
        );
    }
}
