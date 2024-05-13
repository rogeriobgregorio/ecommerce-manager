package com.rogeriogregorio.ecommercemanager.services.strategy.password;

import com.rogeriogregorio.ecommercemanager.services.strategy.PasswordStrategy;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class LowerCaseValidationStrategy implements PasswordStrategy {

    public boolean validatePassword(String password) {

        return Pattern.matches(".*[a-z].*", password);
    }

    public String getRequirement() {
        return "one lowercase letter";
    }
}
