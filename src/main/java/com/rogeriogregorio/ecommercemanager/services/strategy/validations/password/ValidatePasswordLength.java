package com.rogeriogregorio.ecommercemanager.services.strategy.validations.password;

import com.rogeriogregorio.ecommercemanager.services.strategy.validations.PasswordStrategy;
import org.springframework.stereotype.Component;

@Component
public class ValidatePasswordLength implements PasswordStrategy {

    public boolean validatePassword(String password) {

        return password.length() >= 8;
    }

    public String getRequirement() {
        return "eight characters";
    }
}
