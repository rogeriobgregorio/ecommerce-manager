package com.rogeriogregorio.ecommercemanager.services.strategy.password;

import com.rogeriogregorio.ecommercemanager.services.PasswordStrategy;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UpperCaseValidationStrategy implements PasswordStrategy {

    public boolean validate(String password) {
        return Pattern.matches(".*[A-Z].*", password);
    }

    public String getErrorMessage() {
        return "The password must have at least one uppercase letter.";
    }
}
