package com.rogeriogregorio.ecommercemanager.services.strategy.validations;

import org.springframework.stereotype.Component;

@Component
public interface PasswordStrategy {

    boolean validatePassword(String password);

    String getRequirement();
}
