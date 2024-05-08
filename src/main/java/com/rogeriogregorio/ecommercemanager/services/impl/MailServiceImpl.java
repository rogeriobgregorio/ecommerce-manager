package com.rogeriogregorio.ecommercemanager.services.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.exceptions.MailException;
import com.rogeriogregorio.ecommercemanager.exceptions.TokenException;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.services.MailService;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandler;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class MailServiceImpl implements MailService {

    @Value("${api.security.token.secret}")
    private String secretKey;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final ErrorHandler errorHandler;
    private final Converter converter;
    private static final String ISSUER_NAME = "ecommerce-manager";

    @Autowired
    public MailServiceImpl(JavaMailSender mailSender, UserRepository userRepository,
                           ErrorHandler errorHandler, Converter converter) {

        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.errorHandler = errorHandler;
        this.converter = converter;
    }

    public void sendAccountActivationEmail(User user) {

        try {
            String token = generateEmailVerificationToken(user);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            messageHelper.setFrom("ecommercemanager@mailservice.com");
            messageHelper.setSubject("Please activate your account");
            messageHelper.setTo(user.getEmail());

            String emailTemplate = getAccountActivationEmailTemplate();
            emailTemplate = emailTemplate.replace("#{nome}", user.getName());
            emailTemplate = emailTemplate.replace("#{token}", token);

            messageHelper.setText(emailTemplate, true);

            mailSender.send(message);

        } catch (MessagingException ex) {
            throw new MailException("Error when trying to send account activation email: ", ex);
        }
    }

    private String getAccountActivationEmailTemplate() {

        try {
            ClassPathResource pathResource = new ClassPathResource("email-template.html");
            return new String(pathResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException ex) {
            throw new MailException("Error when trying to get account activation email template: ", ex);
        }
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
                        .withClaim("userEmail", user.getEmail())
                        .withExpiresAt(generateExpirationDate())
                        .sign(algorithm),
                "Error generating email verification token");
    }

    public void validateEmailVerificationToken(String token) {

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        DecodedJWT decodedJWT = errorHandler.catchException(() -> JWT
                        .require(algorithm)
                        .withIssuer(ISSUER_NAME)
                        .build()
                        .verify(token),
                "An error occurred when trying to validate the email validation token");


        Long userIdFromToken = decodedJWT.getClaim("userId").asLong();
        User user = converter.toEntity(userRepository.findById(userIdFromToken), User.class);

        String userEmailFromToken = decodedJWT.getClaim("userEmail").asString();
        if (!userEmailFromToken.equals(user.getEmail())) {
            throw new TokenException("");
        }

        Instant expirationDate = decodedJWT.getExpiresAt().toInstant();
        if (!expirationDate.isBefore(Instant.now())) {
            throw new TokenException("The token has expired");
        }

        markEmailAsVerified(user);
    }

    private void markEmailAsVerified(User user) {

        user.setEnabled(true);
        userRepository.save(user);
    }
}
