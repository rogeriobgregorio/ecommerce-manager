package com.rogeriogregorio.ecommercemanager.services.strategy;

import org.springframework.stereotype.Component;

@Component
public interface PasswordStrategy {

    boolean validate(String password);
    String getRequirement();
}
