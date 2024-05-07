package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.exceptions.MailException;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    @Autowired
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAccountActivationEmail(User user) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);

            messageHelper.setFrom("ecommercemanager@mailservice.com");
            messageHelper.setSubject("Please activate your account");
            messageHelper.setTo(user.getEmail());

            String emailTemplate = getAccountActivationEmailTemplate();
            emailTemplate = emailTemplate.replace("#{nome}", user.getName());

            messageHelper.setText(emailTemplate, true);

            mailSender.send(message);

        } catch (MessagingException ex) {
            throw new MailException("Error when trying to send account activation email: ", ex);
        }
    }

    public String getAccountActivationEmailTemplate() {

        try {
            ClassPathResource pathResource = new ClassPathResource("email-template.html");
            return new String(pathResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        } catch (IOException ex) {
            throw new MailException("Error when trying to get account activation email template: ", ex);
        }
    }
}
