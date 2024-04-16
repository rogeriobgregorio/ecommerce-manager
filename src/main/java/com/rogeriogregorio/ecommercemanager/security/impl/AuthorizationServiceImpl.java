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
    public UserDetails loadUserByUsername(String emailLogin) {

        return handleError(() -> userRepository.findByEmail(emailLogin),
                "User not found by login email: " + emailLogin);
    }

    @PostConstruct
    private void createAdminUser() {

        String encryptedPassword = new BCryptPasswordEncoder().encode(secretPassword);
        User adminUser = new User("ADMIN", "admin@email.com", "00000000", encryptedPassword, UserRole.ADMIN);
        userRepository.save(adminUser);
    }
}