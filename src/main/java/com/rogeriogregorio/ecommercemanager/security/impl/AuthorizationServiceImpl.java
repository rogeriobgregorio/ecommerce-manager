package com.rogeriogregorio.ecommercemanager.security.impl;

import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.security.AuthorizationService;
import com.rogeriogregorio.ecommercemanager.utils.catchError;
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
    private final catchError handler;

    @Autowired
    public AuthorizationServiceImpl(UserRepository userRepository,
                                    catchError handler) {

        this.userRepository = userRepository;
        this.handler = handler;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        return handler.run(
                () -> userRepository.findByEmail(email),
                "Error while trying to fetch the user by login email: "
        );
    }

    @PostConstruct
    private void createDefaultAdmin() {

        String encodedPassword = new BCryptPasswordEncoder().encode(secretPassword);

        User admin = User.newBuilder()
                .withName("Admin")
                .withEmail("admin@email.com")
                .withPhone("11912345678")
                .withCpf("72482581052")
                .withPassword(encodedPassword)
                .withRole(UserRole.ADMIN)
                .build();

        handler.run(
                () -> userRepository.save(admin),
                "An error occurred while creating the default admin user"
        );
    }
}