package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.mail.MailService;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.utils.PasswordHelper;
import com.rogeriogregorio.ecommercemanager.services.UserService;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import com.rogeriogregorio.ecommercemanager.utils.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MailService mailService;
    private final PasswordHelper passwordHelper;
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           MailService mailService,
                           PasswordHelper passwordHelper,
                           ErrorHandler errorHandler,
                           DataMapper dataMapper) {

        this.userRepository = userRepository;
        this.mailService = mailService;
        this.passwordHelper = passwordHelper;
        this.errorHandler = errorHandler;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAllUsers(Pageable pageable) {

        return errorHandler.catchException(() -> userRepository.findAll(pageable),
                        "Error while trying to fetch all users: ")
                .map(user -> dataMapper.map(user, UserResponse.class));
    }

    @Transactional(readOnly = true)
    public UserResponse findUserById(UUID id) {

        return errorHandler.catchException(() -> userRepository.findById(id),
                        "Error while trying to fetch the user by ID: " + id)
                .map(user -> dataMapper.map(user, UserResponse.class))
                .orElseThrow(() -> new NotFoundException("User response not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public UserResponse registerUser(UserRequest userRequest) {

        passwordHelper.validate(userRequest.getPassword());
        String encodedPassword = passwordHelper.enconde(userRequest.getPassword());
        User user = dataMapper.map(userRequest, User.class);
        user.setPassword(encodedPassword);
        user.setRole(UserRole.CLIENT);

        user.setEmailEnabled(true);// TODO remover essa linha

        errorHandler.catchException(() -> userRepository.save(user),
                "Error while trying to register the user: ");
        logger.info("User registered: {}", user);

        //CompletableFuture.runAsync(() -> mailService.sendVerificationEmail(user));// TODO reativar método

        return dataMapper.map(user, UserResponse.class);
    }

    @Transactional(readOnly = false)
    public UserResponse updateUser(UUID id, UserRequest userRequest) {

        User currentUser = getUserIfExists(id);
        User updatedUser = dataMapper.map(userRequest, currentUser);
        passwordHelper.validate(userRequest.getPassword());
        String encodedPassword = passwordHelper.enconde(userRequest.getPassword());
        updatedUser.setPassword(encodedPassword);

        errorHandler.catchException(() -> userRepository.save(updatedUser),
                "Error while trying to update the user: ");
        logger.info("User updated: {}", updatedUser);

        return dataMapper.map(updatedUser, UserResponse.class);
    }

    @Transactional(readOnly = false)
    public UserResponse createAdminOrManagerUser(UUID id, UserRequest userRequest) {

        User user = getUserIfExists(id);
        user.setRole(userRequest.getUserRole());

        errorHandler.catchException(() -> userRepository.save(user),
                "Error trying to update user role: ");
        logger.info("User role updated: {}", user);

        return dataMapper.map(user, UserResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteUser(UUID id) {

        User user = getUserIfExists(id);

        errorHandler.catchException(() -> {
            userRepository.delete(user);
            return null;
        }, "Error while trying to delete the user: ");
        logger.warn("User removed: {}", user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findUserByName(String name, Pageable pageable) {

        return errorHandler.catchException(() -> userRepository.findByName(name, pageable),
                        "Error while trying to fetch the user by name: ").
                map(user -> dataMapper.map(user, UserResponse.class));
    }

    public User getUserIfExists(UUID id) {

        return errorHandler.catchException(() -> {

            if (!userRepository.existsById(id)) {
                throw new NotFoundException("User not exists with ID: " + id + ".");
            }

            return dataMapper.map(userRepository.findById(id), User.class);
        }, "Error while trying to verify the existence of the user by ID: ");
    }

    public void saveUserAddress(User user) {

        errorHandler.catchException(() -> {
            userRepository.save(user);
            return null;
        }, "Error while trying to update the user's address: ");
        logger.info("User's address updated: {}", user);
    }
}
