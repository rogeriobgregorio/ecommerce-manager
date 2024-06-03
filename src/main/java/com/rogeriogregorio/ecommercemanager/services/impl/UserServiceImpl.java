package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.PasswordResetDTO;
import com.rogeriogregorio.ecommercemanager.dto.requests.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.PasswordException;
import com.rogeriogregorio.ecommercemanager.mail.MailService;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.services.UserService;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.PasswordStrategy;
import com.rogeriogregorio.ecommercemanager.util.DataMapper;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final List<PasswordStrategy> validators;
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository, MailService mailService,
                           List<PasswordStrategy> validators,
                           ErrorHandler errorHandler, DataMapper dataMapper,
                           PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.mailService = mailService;
        this.validators = validators;
        this.errorHandler = errorHandler;
        this.dataMapper = dataMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAllUsers(Pageable pageable) {

        return errorHandler.catchException(() -> userRepository.findAll(pageable),
                        "Error while trying to fetch all users: ")
                .map(user -> dataMapper.toResponse(user, UserResponse.class));
    }

    @Transactional(readOnly = true)
    public UserResponse findUserResponseById(UUID id) {

        return errorHandler.catchException(() -> userRepository.findById(id),
                        "Error while trying to fetch the user by ID: " + id)
                .map(user -> dataMapper.toResponse(user, UserResponse.class))
                .orElseThrow(() -> new NotFoundException("User response not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public UserResponse registerUser(UserRequest userRequest) {

        userRequest.setId(null);
        User user = buildCreateUser(userRequest);

        user.setEmailEnabled(true);// TODO remover essa linha

        errorHandler.catchException(() -> userRepository.save(user),
                "Error while trying to register the user: ");
        logger.info("User registered: {}", user);

        //CompletableFuture.runAsync(() -> mailService.sendVerificationEmail(user));// TODO reativar método

        return dataMapper.toResponse(user, UserResponse.class);
    }

    @Transactional(readOnly = false)
    public UserResponse updateUser(UserRequest userRequest) {

        isUserExists(userRequest.getId());
        User user = buildUpdateUser(userRequest);

        errorHandler.catchException(() -> userRepository.save(user),
                "Error while trying to update the user: ");
        logger.info("User updated: {}", user);

        return dataMapper.toResponse(user, UserResponse.class);
    }

    @Transactional(readOnly = false)
    public UserResponse createAdminOrManagerUser(UserRequest userRequest) {

        User user = buildAdminOrManagerUser(userRequest);

        errorHandler.catchException(() -> userRepository.save(user),
                "Error trying to update user role: ");
        logger.info("User role updated: {}", user);

        return dataMapper.toResponse(user, UserResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteUser(UUID id) {

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
                map(user -> dataMapper.toResponse(user, UserResponse.class));
    }

    public User findUserById(UUID id) {

        return errorHandler.catchException(() -> userRepository.findById(id),
                        "Error while trying to fetch the user by ID: " + id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id + "."));
    }

    public User findUserByEmail(String email) {

        return errorHandler.catchException(() -> userRepository.findUserByEmail(email),
                        "Error while trying to fetch the user by email: " + email)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email + "."));
    }

    private void isUserExists(UUID id) {

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

    private String validatePassword(String password) {

        List<String> failures = new ArrayList<>();

        for (PasswordStrategy strategy : validators) {
            if (!strategy.validatePassword(password)) {
                failures.add(strategy.getRequirement());
            }
        }

        if (!failures.isEmpty()) {
            throw new PasswordException("The password must have at least: " + failures + ".");
        }

        return passwordEncoder.encode(password);
    }

    private User buildCreateUser(UserRequest userRequest) {

        String encodedPassword = validatePassword(userRequest.getPassword());
        userRequest.setPassword(encodedPassword);
        userRequest.setUserRole(UserRole.CLIENT);

        return dataMapper.toEntity(userRequest, User.class);
    }

    private User buildUpdateUser(UserRequest userRequest) {

        String encodedPassword = validatePassword(userRequest.getPassword());
        userRequest.setPassword(encodedPassword);

        User user = findUserById(userRequest.getId());
        user = dataMapper.transferSkipNull(userRequest, user);

        return user;
    }

    private User buildAdminOrManagerUser(UserRequest userRequest) {

        return findUserById(userRequest.getId()).toBuilder()
                .withRole(userRequest.getUserRole())
                .build();
    }
}
