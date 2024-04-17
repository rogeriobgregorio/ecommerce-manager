package com.rogeriogregorio.ecommercemanager.security.impl;

import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.security.AuthorizationService;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplateImpl;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationServiceImpl extends ErrorHandlerTemplateImpl implements AuthorizationService {

    @Value("${api.security.password.secret}")
    private String secretPassword;

    private final UserRepository userRepository;

    @Autowired
    public AuthorizationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        return handleError(() -> userRepository.findByEmail(email),
                "User not found by login email: " + email);
    }

    @PostConstruct
    private void createDefaultAdminUser() {

        String encryptedPassword = new BCryptPasswordEncoder().encode(secretPassword);
        User adminUser = new User("Admin", "admin@email.com", "00000000", encryptedPassword, UserRole.ADMIN);
        handleError(() -> userRepository.save(adminUser),
                "An error occurred while creating the default admin user");
    }
}