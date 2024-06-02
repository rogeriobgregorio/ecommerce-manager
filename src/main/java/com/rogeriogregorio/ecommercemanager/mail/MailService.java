package com.rogeriogregorio.ecommercemanager.mail;

import com.rogeriogregorio.ecommercemanager.dto.PasswordResetDTO;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.User;
import org.springframework.stereotype.Component;

@Component
public interface MailService {

    void sendVerificationEmail(User user);

    void sendPasswordResetEmail(PasswordResetDTO PasswordResetDTO);

    void validatePasswordResetToken(PasswordResetDTO passwordResetDTO);

    UserResponse validateEmailVerificationToken(String token);
}
