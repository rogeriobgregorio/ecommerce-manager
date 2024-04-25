package com.rogeriogregorio.ecommercemanager.services.strategy.password;

import com.rogeriogregorio.ecommercemanager.services.strategy.PasswordStrategy;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class NumberValidationStrategy implements PasswordStrategy {

    public boolean validate(String password) {
        return Pattern.matches(".*\\d.*", password);
    }

    public String getRequirement() {
        return "one number";
    }
}
