package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.services.AuthorizationService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private final UserRepository userRepository;

    @Autowired
    public AuthorizationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String emailLogin) throws UsernameNotFoundException {

        return userRepository.findByEmail(emailLogin);
    }

    @PostConstruct
    private void createAdminUser() {
        if (userRepository.findByEmail("admin@email.com") == null) {
            String encryptedPassword = new BCryptPasswordEncoder().encode("adminpassword");
            User adminUser = new User("ADMIN", "admin@email.com", "00000000", encryptedPassword, UserRole.ADMIN);
            userRepository.save(adminUser);
        }
    }
}
