package com.rogeriogregorio.ecommercemanager.services;

import org.springframework.stereotype.Component;

@Component
public interface PasswordStrategy {

    boolean validate(String password);
    String getErrorMessage();
}
