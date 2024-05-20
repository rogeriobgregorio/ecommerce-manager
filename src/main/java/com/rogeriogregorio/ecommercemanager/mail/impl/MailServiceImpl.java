package com.rogeriogregorio.ecommercemanager.mail.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.TokenJwtException;
import com.rogeriogregorio.ecommercemanager.mail.MailService;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.util.DataMapper;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class MailServiceImpl implements MailService {

    @Value("${api.security.token.secret}")
    private String secretKey;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private static final String ISSUER_NAME = "ecommerce-manager";
    private final Logger logger = LogManager.getLogger(MailServiceImpl.class);

    @Autowired
    public MailServiceImpl(JavaMailSender mailSender, UserRepository userRepository,
                           ErrorHandler errorHandler, DataMapper dataMapper) {

        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.errorHandler = errorHandler;
        this.dataMapper = dataMapper;
    }

    private Instant generateExpirationDate() {

        return Instant.now().plus(2, ChronoUnit.HOURS);
    }

    public void sendVerificationEmail(User user) {

        String token = generateEmailVerificationToken(user);

        errorHandler.catchException(() -> {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            messageHelper.setFrom("ecommercemanager@mailservice.com");
            messageHelper.setSubject("Email Verification Process");
            messageHelper.setTo(user.getEmail());

            String emailTemplate = getVerificationEmailTemplate();
            emailTemplate = emailTemplate.replace("#{name}", user.getName());
            emailTemplate = emailTemplate.replace("#{token}", token);

            messageHelper.setText(emailTemplate, true);

            mailSender.send(message);
            logger.info("Verification email sent to: {}", user.getEmail());

            return null;
        }, "Error when trying to send account activation email: ");
    }

    private String getVerificationEmailTemplate() {

        ClassPathResource pathResource = new ClassPathResource("templates/verification-email.html");

        return errorHandler.catchException(() -> new String(pathResource
                .getInputStream()
                .readAllBytes(), StandardCharsets.UTF_8),
                "Error while trying to get verification email template: ");
    }

    public String generateEmailVerificationToken(User user) {

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return errorHandler.catchException(() -> JWT
                        .create()
                        .withIssuer(ISSUER_NAME)
                        .withSubject(user.getEmail())
                        .withClaim("userId", String.valueOf(user.getId()))
                        .withClaim("userEmail", user.getEmail())
                        .withExpiresAt(generateExpirationDate())
                        .sign(algorithm),
                "Error while trying to generate email verification token");
    }

    @Transactional(readOnly = false)
    public UserResponse validateEmailVerificationToken(String token) {

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        DecodedJWT decodedJWT = errorHandler.catchException(() -> JWT
                        .require(algorithm)
                        .withIssuer(ISSUER_NAME)
                        .build()
                        .verify(token),
                "Error while trying to validate the email validation token");

        String userIdFromToken = decodedJWT.getClaim("userId").asString();
        User user = findUserByIdFromToken(userIdFromToken);

        String userEmailFromToken = decodedJWT.getClaim("userEmail").asString();
        if (!userEmailFromToken.equals(user.getEmail())) {
            throw new NotFoundException("The user with the token email was not found");
        }

        Instant expirationDate = decodedJWT.getExpiresAt().toInstant();
        if (expirationDate.isBefore(Instant.now())) {
            throw new TokenJwtException("Email verification token has expired");
        }

        saveEmailAsEnabled(user);
        return dataMapper.toResponse(user, UserResponse.class);
    }

    private User findUserByIdFromToken(String userIdFromToken) {

        return errorHandler.catchException(() -> userRepository.findById(UUID.fromString(userIdFromToken)),
                        "Error while trying to search for user by token ID: " + userIdFromToken)
                .orElseThrow(() -> new NotFoundException("The user with the token ID was not found"));
    }

    private void saveEmailAsEnabled(User user) {

        user.setEmailEnabled(true);
        errorHandler.catchException(() -> userRepository.save(user),
                "Error while trying to save verified email");
        logger.info("User email {} verified and saved", user.getEmail());
    }
}