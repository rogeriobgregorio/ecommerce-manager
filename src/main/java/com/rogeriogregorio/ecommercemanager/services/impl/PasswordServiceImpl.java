package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.exceptions.PasswordException;
import com.rogeriogregorio.ecommercemanager.services.PasswordService;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.PasswordStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PasswordServiceImpl implements PasswordService {

    private final PasswordEncoder passwordEncoder;
    private final List<PasswordStrategy> validators;

    public PasswordServiceImpl(PasswordEncoder passwordEncoder,
                               List<PasswordStrategy> validators) {

        this.passwordEncoder = passwordEncoder;
        this.validators = validators;
    }

    public void validate(String password) {

        List<String> failures = new ArrayList<>();

        for (PasswordStrategy strategy : validators) {
            if (!strategy.validatePassword(password)) {
                failures.add(strategy.getRequirement());
            }
        }

        if (!failures.isEmpty()) {
            throw new PasswordException("The password must have at least: " + failures + ".");
        }
    }

    public String enconde(String password) {
        return passwordEncoder.encode(password);
    }
}
