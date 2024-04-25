package com.rogeriogregorio.ecommercemanager.services.strategy.password;

import com.rogeriogregorio.ecommercemanager.services.strategy.PasswordStrategy;
import org.springframework.stereotype.Component;

@Component
public class LengthValidationStrategy implements PasswordStrategy {

    public boolean validate(String password) {
        return password.length() >= 8;
    }

    public String getRequirement() {
        return "eight characters";
    }
}
