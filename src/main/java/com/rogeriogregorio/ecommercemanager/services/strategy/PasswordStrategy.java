package com.rogeriogregorio.ecommercemanager.services.strategy;

import org.springframework.stereotype.Component;

@Component
public interface PasswordStrategy {

    boolean validatePassword(String password);
    String getRequirement();
}
