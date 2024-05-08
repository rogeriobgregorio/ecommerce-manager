package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.PasswordException;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.services.MailService;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandler;
import com.rogeriogregorio.ecommercemanager.services.strategy.PasswordStrategy;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements com.rogeriogregorio.ecommercemanager.services.UserService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final List<PasswordStrategy> validators;
    private final ErrorHandler errorHandler;
    private final Converter converter;
    private final Logger logger = LogManager.getLogger();

    @Autowired
    public UserServiceImpl(UserRepository userRepository, MailService mailService,
                           List<PasswordStrategy> validators,
                           ErrorHandler errorHandler, Converter converter) {

        this.userRepository = userRepository;
        this.mailService = mailService;
        this.validators = validators;
        this.errorHandler = errorHandler;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAllUsers(Pageable pageable) {

        return errorHandler.catchException(() -> userRepository.findAll(pageable),
                "Error while trying to fetch all users: ")
                .map(user -> converter.toResponse(user, UserResponse.class));
    }

    @Transactional(readOnly = true)
    public UserResponse findUserResponseById(Long id) {

        return errorHandler.catchException(() -> userRepository.findById(id),
                "Error while trying to fetch the user by ID: " + id)
                .map(user -> converter.toResponse(user, UserResponse.class))
                .orElseThrow(() -> new NotFoundException("User response not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public UserResponse createUser(UserRequest userRequest) {

        userRequest.setId(null);
        User user = buildUser(userRequest);

        errorHandler.catchException(() -> userRepository.save(user),
                "Error while trying to create the user: ");
        logger.info("User created: {}", user);

        mailService.sendAccountActivationEmail(user);
        return converter.toResponse(user, UserResponse.class);
    }

    @Transactional(readOnly = false)
    public UserResponse updateUser(UserRequest userRequest) {

        isUserExists(userRequest.getId());
        User user = buildUser(userRequest);

        errorHandler.catchException(() -> userRepository.save(user),
                "Error while trying to update the user: ");
        logger.info("User updated: {}", user);

        return converter.toResponse(user, UserResponse.class);
    }

    @Transactional(readOnly = false)
    public UserResponse createAdminOrManagerUser(UserRequest userRequest) {

        User user = buildAdminOrManagerUser(userRequest);

        errorHandler.catchException(() -> userRepository.save(user),
                "Error trying to update user role: ");
        logger.info("User updated: {}", user);

        return converter.toResponse(user, UserResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteUser(Long id) {

        isUserExists(id);

        errorHandler.catchException(() -> {
            userRepository.deleteById(id);
            return null;
        }, "Error while trying to delete the user: ");
        logger.warn("User removed: {}", id);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findUserByName(String name, Pageable pageable) {

        return errorHandler.catchException(() -> userRepository.findByName(name, pageable),
                "Error while trying to fetch the user by name: ").
                map(user -> converter.toResponse(user, UserResponse.class));
    }

    public User findUserById(Long id) {

        return errorHandler.catchException(() -> userRepository.findById(id),
                "Error while trying to fetch the user by ID: " + id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id + "."));
    }

    private void isUserExists(Long id) {

        boolean isUserExists = errorHandler.catchException(() -> userRepository.existsById(id),
                "Error while trying to check the presence of the user: ");

        if (!isUserExists) {
            throw new NotFoundException("User not found with ID: " + id + ".");
        }
    }

    public void saveUserAddress(User user) {

        errorHandler.catchException(() -> {
            userRepository.save(user);
            return null;
        }, "Error while trying to update the user's address: ");
        logger.info("User's address updated: {}", user);
    }

    public void saveVerifiedEmail(User user) {

        errorHandler.catchException(() -> {
            userRepository.save(user);
            return null;
        }, "Error while trying to save the user's verified email: " + user);
        logger.info("saved user's verified email: {}", user);
    }

    private String validatePassword(String password) {

        List<String> failures = new ArrayList<>();

        for (PasswordStrategy strategy : validators) {
            if (!strategy.validate(password)) {
                failures.add(strategy.getRequirement());
            }
        }

        if (!failures.isEmpty()) {
            throw new PasswordException("The password must have at least: " + failures + ".");
        }

        return new BCryptPasswordEncoder().encode(password);
    }

    private User buildUser(UserRequest userRequest) {

        String encodedPassword = validatePassword(userRequest.getPassword());
        userRequest.setPassword(encodedPassword);
        userRequest.setUserRole(UserRole.CLIENT);

        return converter.toEntity(userRequest, User.class);
    }

    private User buildAdminOrManagerUser(UserRequest userRequest) {

        User user = findUserById(userRequest.getId());
        user.setRole(userRequest.getUserRole());

        return user;
    }
}
