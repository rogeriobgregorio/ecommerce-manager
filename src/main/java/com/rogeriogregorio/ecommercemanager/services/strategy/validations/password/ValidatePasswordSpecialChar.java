package com.rogeriogregorio.ecommercemanager.services.strategy.validations.password;

import com.rogeriogregorio.ecommercemanager.services.strategy.validations.PasswordStrategy;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ValidatePasswordSpecialChar implements PasswordStrategy {

    public boolean validatePassword(String password) {
        return Pattern.matches(".*\\W.*", password);
    }

    public String getRequirement() {
        return "one special character";
    }
}
