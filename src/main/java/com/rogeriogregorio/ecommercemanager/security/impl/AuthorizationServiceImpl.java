package com.rogeriogregorio.ecommercemanager.security.impl;

import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.security.AuthorizationService;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
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
    private final ErrorHandler handler;

    @Autowired
    public AuthorizationServiceImpl(UserRepository userRepository, ErrorHandler handler) {
        this.userRepository = userRepository;
        this.handler = handler;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        return handler.catchException(() -> userRepository.findByEmail(email),
                "Error while trying to fetch the user by login email: " + email);
    }

    @PostConstruct
    private void createDefaultAdmin() {

        String password = new BCryptPasswordEncoder().encode(secretPassword);

        User admin = User.newBuilder()
                .withName("Admin")
                .withEmail("admin@gmail.com")
                .withPhone("11912345678")
                .withCpf("72482581052")
                .withPassword(password)
                .withRole(UserRole.ADMIN)
                .build();

        handler.catchException(() -> userRepository.save(admin),
                "An error occurred while creating the default admin user");
    }
}