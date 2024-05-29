package com.rogeriogregorio.ecommercemanager.services.strategy.validations.password;

import com.rogeriogregorio.ecommercemanager.services.strategy.validations.PasswordStrategy;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ValidatePasswordLowerCase implements PasswordStrategy {

    public boolean validatePassword(String password) {

        return Pattern.matches(".*[a-z].*", password);
    }

    public String getRequirement() {
        return "one lowercase letter";
    }
}
