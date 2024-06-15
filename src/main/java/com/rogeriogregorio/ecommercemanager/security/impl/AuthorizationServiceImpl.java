package com.rogeriogregorio.ecommercemanager.security.impl;

import com.rogeriogregorio.ecommercemanager.dto.UserAuthDetailsDto;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.security.AuthorizationService;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    @Value("${api.security.password.secret}")
    private String secretPassword;
    private final UserRepository userRepository;
    private final CatchError catchError;

    @Autowired
    public AuthorizationServiceImpl(UserRepository userRepository,
                                    CatchError handler) {

        this.userRepository = userRepository;
        this.catchError = handler;
    }

    @Override
    public UserAuthDetailsDto loadUserByUsername(String email) {

        return catchError.run(() -> userRepository.findByEmail(email))
                .map(UserAuthDetailsDto::new)
                .orElseThrow(() -> new RuntimeException("User cannot be loaded by email"));
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

        catchError.run(() -> userRepository.save(admin));
    }
}