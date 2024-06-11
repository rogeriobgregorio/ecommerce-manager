package com.rogeriogregorio.ecommercemanager.mail.impl;

import com.rogeriogregorio.ecommercemanager.dto.EmailDetailsDto;
import com.rogeriogregorio.ecommercemanager.dto.PasswordResetDto;
import com.rogeriogregorio.ecommercemanager.dto.ReceiptPaymentDto;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.Payment;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.mail.MailService;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.security.TokenService;
import com.rogeriogregorio.ecommercemanager.utils.PasswordHelper;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailServiceImpl implements MailService {

    private static final String SENDER_EMAIL = "ecommercemanager@mailservice.com";
    private static final String RECEIPT_PAYMENT = "Receipt of Payment";
    private static final String EMAIL_VERIFICATION_PROCESS = "Email Verification Process";
    private static final String PASSWORD_RESET_PROCESS = "Password Reset Process";
    private static final String RECEIPT_PAYMENT_HTML = "templates/receipt-payment-email.html";
    private static final String VERIFICATION_EMAIL_HTML = "templates/verification-email.html";
    private static final String PASSWORD_RESET_HTML = "templates/password-reset-email.html";

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final PasswordHelper passwordHelper;
    private final TokenService tokenService;
    private final CatchError catchError;
    private final DataMapper dataMapper;
    private static final Logger LOGGER = LogManager.getLogger(MailServiceImpl.class);

    @Autowired
    public MailServiceImpl(JavaMailSender mailSender, UserRepository userRepository,
                           PasswordHelper passwordHelper, TokenService tokenService,
                           CatchError catchError, DataMapper dataMapper) {

        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.passwordHelper = passwordHelper;
        this.tokenService = tokenService;
        this.catchError = catchError;
        this.dataMapper = dataMapper;
    }

    private void sendEmail(EmailDetailsDto emailDetails) {

        catchError.run(() -> {

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
            LOGGER.info("Email sent to: {}", emailDetails.getRecipient());

            return null;
        });
    }

    public void sendVerificationEmail(User user) {

        String token = tokenService.generateEmailToken(user);

        Map<String, String> replacements = new HashMap<>();
        replacements.put("#{name}", user.getName());
        replacements.put("#{token}", token);

        sendEmail(EmailDetailsDto.newBuilder()
                .withSubject(EMAIL_VERIFICATION_PROCESS)
                .withRecipient(user.getEmail())
                .withTemplateName(VERIFICATION_EMAIL_HTML)
                .withReplacements(replacements)
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
                .build()
        );
    }

    public void sendPasswordResetEmail(PasswordResetDto passwordReset) {

        User user = findUserByEmail(passwordReset.getEmail());
        String token = tokenService.generateEmailToken(user);

        Map<String, String> replacements = new HashMap<>();
        replacements.put("#{name}", user.getName());
        replacements.put("#{token}", token);

        sendEmail(EmailDetailsDto.newBuilder()
                .withSubject(PASSWORD_RESET_PROCESS)
                .withRecipient(user.getEmail())
                .withTemplateName(PASSWORD_RESET_HTML)
                .withReplacements(replacements)
                .build()
        );
    }

    private String getEmailTemplate(String path) {

        ClassPathResource pathResource = new ClassPathResource(path);

        return catchError.run(() -> new String(pathResource.getInputStream()
                .readAllBytes(), StandardCharsets.UTF_8));
    }

    public UserResponse validateEmailVerificationToken(String token) {

        User user = tokenService.validateEmailToken(token);

        saveEmailAsEnabled(user);
        return dataMapper.map(user, UserResponse.class);
    }

    public void validatePasswordResetToken(PasswordResetDto passwordReset) {

        User user = tokenService.validateEmailToken(passwordReset.getToken());

        user.setPassword(passwordReset.getPassword());
        saveNewPassword(user);
    }

    @Transactional(readOnly = true)
    private User findUserByEmail(String email) {

        return catchError.run(() -> userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("The user was not found with the email: " + email)));
    }

    @Transactional(readOnly = false)
    private void saveEmailAsEnabled(User user) {

        user.setEmailEnabled(true);

        User savedUser = catchError.run(() -> userRepository.save(user));
        LOGGER.info("User email verified and saved: {}", savedUser.getEmail());
    }

    @Transactional(readOnly = false)
    public void saveNewPassword(User user) {

        passwordHelper.validate(user.getPassword());
        String passwordEncode = passwordHelper.enconde(user.getPassword());
        user.setPassword(passwordEncode);

        User savedUser = catchError.run(() -> userRepository.save(user));
        LOGGER.info("User password updated: {}", savedUser);
    }
}