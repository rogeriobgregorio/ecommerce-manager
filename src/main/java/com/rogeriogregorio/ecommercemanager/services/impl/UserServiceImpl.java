package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.mail.MailService;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.services.UserService;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import com.rogeriogregorio.ecommercemanager.utils.PasswordHelper;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final PasswordHelper passwordHelper;
    private final CatchError catchError;
    private final DataMapper dataMapper;
    private static final Logger LOGGER = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           MailService mailService,
                           PasswordHelper passwordHelper,
                           CatchError catchError,
                           DataMapper dataMapper) {

        this.userRepository = userRepository;
        this.mailService = mailService;
        this.passwordHelper = passwordHelper;
        this.catchError = catchError;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAllUsers(Pageable pageable) {

        return catchError.run(() -> userRepository.findAll(pageable))
                .map(user -> dataMapper.map(user, UserResponse.class));
    }

    @Transactional
    public UserResponse registerUser(UserRequest userRequest) {

        passwordHelper.validate(userRequest.getPassword());
        String encodedPassword = passwordHelper.enconde(userRequest.getPassword());
        User user = dataMapper.map(userRequest, User.class);
        user.setPassword(encodedPassword);
        user.setRole(UserRole.CLIENT);

        user.setEmailEnabled(true); // TODO remover essa linha

        User savedUser = catchError.run(() -> userRepository.save(user));
        LOGGER.info("User registered: {}", savedUser);

//        CompletableFuture.runAsync(() -> mailService.sendVerificationEmail(user)); // TODO reativar método
        return dataMapper.map(savedUser, UserResponse.class);
    }

    @Transactional(readOnly = true)
    public UserResponse findUserById(UUID id) {

        return catchError.run(() -> userRepository.findById(id))
                .map(user -> dataMapper.map(user, UserResponse.class))
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id + "."));
    }

    @Transactional
    public UserResponse updateUser(UUID id, UserRequest userRequest) {

        User currentUser = getUserIfExists(id);
        dataMapper.map(userRequest, currentUser);
        passwordHelper.validate(userRequest.getPassword());
        String encodedPassword = passwordHelper.enconde(userRequest.getPassword());
        currentUser.setPassword(encodedPassword);

        User updatedUser = catchError.run(() -> userRepository.save(currentUser));
        LOGGER.info("User updated: {}", updatedUser);
        return dataMapper.map(updatedUser, UserResponse.class);
    }

    @Transactional
    public UserResponse createAdminOrManagerUser(UUID id, UserRequest userRequest) {

        User user = getUserIfExists(id);
        user.setRole(userRequest.getUserRole());

        User updatedUser = catchError.run(() -> userRepository.save(user));
        LOGGER.info("User role updated: {}", updatedUser);
        return dataMapper.map(updatedUser, UserResponse.class);
    }

    @Transactional
    public void deleteUser(UUID id) {

        User user = getUserIfExists(id);

        catchError.run(() -> userRepository.delete(user));
        LOGGER.warn("User removed: {}", user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findUserByName(String name, Pageable pageable) {

        return catchError.run(() -> userRepository.findByName(name, pageable))
                .map(user -> dataMapper.map(user, UserResponse.class));
    }

    public User getUserIfExists(UUID id) {

        return catchError.run(() -> userRepository.findById(id))
                .orElseThrow(() -> new NotFoundException("User response not found with ID: " + id + "."));
    }

    public void saveUserAddress(User user) {

        User savedUser = catchError.run(() -> userRepository.save(user));
        LOGGER.info("User's address updated: {}", savedUser);
    }
}
