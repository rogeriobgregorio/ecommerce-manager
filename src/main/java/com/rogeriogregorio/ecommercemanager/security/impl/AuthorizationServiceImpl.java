package com.rogeriogregorio.ecommercemanager.security.impl;

import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.security.AuthorizationService;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplate;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    @Value("${api.security.password.secret}")
    private String secretPassword;
    private final UserRepository userRepository;
    private final ErrorHandlerTemplate handler;

    @Autowired
    public AuthorizationServiceImpl(UserRepository userRepository, ErrorHandlerTemplate handler) {
        this.userRepository = userRepository;
        this.handler = handler;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        return handler.catchException(() -> userRepository.findByEmail(email),
                "Error while trying to fetch the user by login email: " + email);
    }

    @PostConstruct
    private void createDefaultAdminUser() {

        String encryptedPassword = new BCryptPasswordEncoder().encode(secretPassword);
        User adminUser = new User("Admin", "admin@email.com", "11912345678", encryptedPassword, UserRole.ADMIN);

        handler.catchException(() -> userRepository.save(adminUser),
                "An error occurred while creating the default admin user");
    }
}