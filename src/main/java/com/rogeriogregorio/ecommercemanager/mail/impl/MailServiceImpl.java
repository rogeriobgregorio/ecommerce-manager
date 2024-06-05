package com.rogeriogregorio.ecommercemanager.mail.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rogeriogregorio.ecommercemanager.dto.EmailDetailsDto;
import com.rogeriogregorio.ecommercemanager.dto.PasswordResetDto;
import com.rogeriogregorio.ecommercemanager.dto.ReceiptPaymentDto;
import com.rogeriogregorio.ecommercemanager.dto.TokenClaimContextDto;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.Payment;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.PasswordException;
import com.rogeriogregorio.ecommercemanager.mail.MailService;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.PasswordStrategy;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.TokenClaimStrategy;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class MailServiceImpl implements MailService {

    private static final String ISSUER_NAME = "ecommerce-manager";
    private static final String SENDER_EMAIL = "ecommercemanager@mailservice.com";
    private static final String RECEIPT_PAYMENT = "Receipt of Payment";
    private static final String EMAIL_VERIFICATION_PROCESS = "Email Verification Process";
    private static final String PASSWORD_RESET_PROCESS = "Password Reset Process";
    private static final String RECEIPT_PAYMENT_HTML = "templates/receipt-payment-email.html";
    private static final String VERIFICATION_EMAIL_HTML = "templates/verification-email.html";
    private static final String PASSWORD_RESET_HTML = "templates/password-reset-email.html";
    private static final Instant EXPIRATION_DATE = Instant.now().plus(2, ChronoUnit.HOURS);

    @Value("${api.security.token.secret}")
    private String secretKey;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final List<PasswordStrategy> passwordValidators;
    private final List<TokenClaimStrategy> tokenValidators;
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private final Logger logger = LogManager.getLogger(MailServiceImpl.class);

    @Autowired
    public MailServiceImpl(JavaMailSender mailSender,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           List<PasswordStrategy> passwordValidators,
                           List<TokenClaimStrategy> tokenValidators,
                           ErrorHandler errorHandler,
                           DataMapper dataMapper) {

        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidators = passwordValidators;
        this.tokenValidators = tokenValidators;
        this.errorHandler = errorHandler;
        this.dataMapper = dataMapper;
    }

    private void sendEmail(EmailDetailsDto emailDetails) {

        errorHandler.catchException(() -> {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            messageHelper.setFrom(SENDER_EMAIL);
            messageHelper.setSubject(emailDetails.getSubject());
            messageHelper.setTo(emailDetails.getRecipient());

            String emailTemplate = getEmailTemplate(emailDetails.getTemplateName());
            for (Map.Entry<String, String> entry : emailDetails.getReplacements().entrySet()) {
                emailTemplate = emailTemplate.replace(entry.getKey(), entry.getValue());
            }

            messageHelper.setText(emailTemplate, true);
            mailSender.send(message);

            logger.info("Email sent to: {}", emailDetails.getRecipient());
            return null;
        }, emailDetails.getErrorMessage());
    }

    public void sendVerificationEmail(User user) {

        String token = generateEmailToken(user);

        Map<String, String> replacements = new HashMap<>();
        replacements.put("#{name}", user.getName());
        replacements.put("#{token}", token);

        sendEmail(EmailDetailsDto.newBuilder()
                .withSubject(EMAIL_VERIFICATION_PROCESS)
                .withRecipient(user.getEmail())
                .withTemplateName(VERIFICATION_EMAIL_HTML)
                .withReplacements(replacements)
                .withErrorMessage("Error while trying to send verification email")
                .build()
        );
    }

    public void sendPaymentReceiptEmail(Payment payment) {

        ReceiptPaymentDto receiptPayment = new ReceiptPaymentDto(payment);

        Map<String, String> replacements = new HashMap<>();
        replacements.put("#{receipt}", receiptPayment.toString());

        sendEmail(EmailDetailsDto.newBuilder()
                .withSubject(RECEIPT_PAYMENT)
                .withRecipient(receiptPayment.getClienteEmail())
                .withTemplateName(RECEIPT_PAYMENT_HTML)
                .withReplacements(replacements)
                .withErrorMessage("Error while trying to send email with payment receipt")
                .build()
        );
    }

    public void sendPasswordResetEmail(PasswordResetDto passwordReset) {

        User user = findUserByEmail(passwordReset.getEmail());
        String token = generateEmailToken(user);

        Map<String, String> replacements = new HashMap<>();
        replacements.put("#{name}", user.getName());
        replacements.put("#{token}", token);

        sendEmail(EmailDetailsDto.newBuilder()
                .withSubject(PASSWORD_RESET_PROCESS)
                .withRecipient(user.getEmail())
                .withTemplateName(PASSWORD_RESET_HTML)
                .withReplacements(replacements)
                .withErrorMessage("Error while trying to send password reset email")
                .build()
        );
    }

    private String getEmailTemplate(String path) {

        ClassPathResource pathResource = new ClassPathResource(path);

        return errorHandler.catchException(
                () -> new String(pathResource.getInputStream()
                        .readAllBytes(), StandardCharsets.UTF_8),
                "Error while trying to get email template: " + path
        );
    }

    private String generateEmailToken(User user) {

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return errorHandler.catchException(
                () -> JWT.create()
                        .withIssuer(ISSUER_NAME)
                        .withSubject(user.getEmail())
                        .withClaim("userId", String.valueOf(user.getId()))
                        .withClaim("userEmail", user.getEmail())
                        .withClaim("userPassword", user.getPassword())
                        .withExpiresAt(EXPIRATION_DATE)
                        .sign(algorithm),
                "Error while trying to generate email verification token"
        );
    }

    public UserResponse validateEmailVerificationToken(String token) {

        User user = validateToken(token);

        saveEmailAsEnabled(user);
        return dataMapper.toResponse(user, UserResponse.class);
    }

    public void validatePasswordResetToken(PasswordResetDto passwordReset) {

        User user = validateToken(passwordReset.getToken());

        user.setPassword(passwordReset.getPassword());
        saveNewPassword(user);
    }

    private User validateToken(String token) {

        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        DecodedJWT decodedJWT = errorHandler.catchException(
                () -> JWT.require(algorithm)
                        .withIssuer(ISSUER_NAME)
                        .build()
                        .verify(token),
                "Error while trying to validate the token"
        );

        String userIdFromToken = decodedJWT.getClaim("userId").asString();
        User user = findUserByIdFromToken(userIdFromToken);

        validateTokenClaim(new TokenClaimContextDto(user, decodedJWT));

        return user;
    }

    @Transactional(readOnly = true)
    private User findUserByIdFromToken(String userIdFromToken) {

        return errorHandler.catchException(() -> userRepository.findById(UUID.fromString(userIdFromToken)),
                        "Error while trying to search for user by token ID: " + userIdFromToken)
                .orElseThrow(() -> new NotFoundException("The user with the token ID was not found"));
    }

    @Transactional(readOnly = true)
    private User findUserByEmail(String email) {

        return errorHandler.catchException(() -> userRepository.findUserByEmail(email),
                        "Error while trying to search for user by email: " + email)
                .orElseThrow(() -> new NotFoundException("The user with the email was not found"));
    }

    @Transactional(readOnly = false)
    private void saveEmailAsEnabled(User user) {

        user.setEmailEnabled(true);
        errorHandler.catchException(() -> userRepository.save(user),
                "Error while trying to save verified email");
        logger.info("User email {} verified and saved", user.getEmail());
    }

    @Transactional(readOnly = false)
    public void saveNewPassword(User user) {

        String passwordEncode = validatePassword(user.getPassword());
        user.setPassword(passwordEncode);
        errorHandler.catchException(() -> userRepository.save(user),
                "Error while trying to update user password: ");
        logger.info("User password updated: {}", user.toString());
    }

    private void validateTokenClaim(TokenClaimContextDto tokenClaimContext) {

        tokenValidators.forEach(strategy -> strategy.validateTokenClaim(tokenClaimContext));
    }

    private String validatePassword(String password) {

        List<String> failures = new ArrayList<>();

        for (PasswordStrategy strategy : passwordValidators) {
            if (!strategy.validatePassword(password)) {
                failures.add(strategy.getRequirement());
            }
        }

        if (!failures.isEmpty()) {
            throw new PasswordException("The password must have at least: " + failures + ".");
        }

        return passwordEncoder.encode(password);
    }
}