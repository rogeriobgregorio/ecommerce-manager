package com.rogeriogregorio.ecommercemanager.services.strategy.validations.password;

import com.rogeriogregorio.ecommercemanager.services.strategy.validations.PasswordStrategy;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ValidatePasswordNumber implements PasswordStrategy {

    public boolean validatePassword(String password) {
        
        return Pattern.matches(".*\\d.*", password);
    }

    public String getRequirement() {
        return "one number";
    }
}
