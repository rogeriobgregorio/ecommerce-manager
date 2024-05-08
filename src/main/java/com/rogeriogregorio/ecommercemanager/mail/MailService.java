package com.rogeriogregorio.ecommercemanager.mail;

import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.User;
import org.springframework.stereotype.Component;

@Component
public interface MailService {

    void sendVerificationEmail(User user);

    String generateEmailVerificationToken(User user);

    UserResponse validateEmailVerificationToken(String token);
}
