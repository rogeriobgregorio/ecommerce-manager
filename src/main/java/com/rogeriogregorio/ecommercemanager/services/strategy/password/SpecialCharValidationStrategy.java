package com.rogeriogregorio.ecommercemanager.services.strategy.password;

import com.rogeriogregorio.ecommercemanager.services.PasswordStrategy;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class SpecialCharValidationStrategy implements PasswordStrategy {

    public boolean validate(String password) {
        return Pattern.matches(".*\\W.*", password);
    }

    public String getRequirement() {
        return "one special character";
    }
}
