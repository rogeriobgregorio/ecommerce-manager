package com.rogeriogregorio.ecommercemanager.services.strategy.password;

import com.rogeriogregorio.ecommercemanager.services.PasswordStrategy;
import org.springframework.stereotype.Component;

@Component
public class LengthValidationStrategy implements PasswordStrategy {

    public boolean validate(String password) {
        return password != null && password.length() >= 8;
    }

    public String getErrorMessage() {
        return "The password must have at least 8 characters.";
    }
}
