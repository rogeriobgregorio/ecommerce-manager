package com.rogeriogregorio.ecommercemanager.services;

import org.springframework.stereotype.Component;

@Component
public interface PasswordService {

    void validate(String password);

    String enconde(String password);
}
