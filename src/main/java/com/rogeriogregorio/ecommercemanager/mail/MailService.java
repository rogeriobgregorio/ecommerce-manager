package com.rogeriogregorio.ecommercemanager.mail;

import com.rogeriogregorio.ecommercemanager.dto.PasswordResetDto;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.Payment;
import com.rogeriogregorio.ecommercemanager.entities.User;
import org.springframework.stereotype.Component;

@Component
public interface MailService {

    void sendPaymentReceiptEmail(Payment payment);

    void sendVerificationEmail(User user);

    void sendPasswordResetEmail(PasswordResetDto PasswordResetDTO);

    void validatePasswordResetToken(PasswordResetDto passwordResetDTO);

    UserResponse validateEmailVerificationToken(String token);
}
