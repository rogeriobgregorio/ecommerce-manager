package com.rogeriogregorio.ecommercemanager.utils.impl;

import com.rogeriogregorio.ecommercemanager.exceptions.PasswordException;
import com.rogeriogregorio.ecommercemanager.utils.PasswordHelper;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.PasswordStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PasswordHelperImpl implements PasswordHelper {

    private final PasswordEncoder passwordEncoder;
    private final List<PasswordStrategy> passwordValidators;

    public PasswordHelperImpl(PasswordEncoder passwordEncoder,
                              List<PasswordStrategy> passwordValidators) {

        this.passwordEncoder = passwordEncoder;
        this.passwordValidators = passwordValidators;
    }

    public void validate(String password) {

        List<String> failures = new ArrayList<>();

        for (PasswordStrategy strategy : passwordValidators) {
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
