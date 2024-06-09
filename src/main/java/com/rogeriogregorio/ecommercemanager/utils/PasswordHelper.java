package com.rogeriogregorio.ecommercemanager.utils;

import org.springframework.stereotype.Component;

@Component
public interface PasswordHelper {

    void validate(String password);

    String enconde(String password);
}
