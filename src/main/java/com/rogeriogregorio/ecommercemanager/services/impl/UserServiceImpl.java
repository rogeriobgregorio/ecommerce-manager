package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.PasswordException;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.services.PasswordStrategy;
import com.rogeriogregorio.ecommercemanager.services.UserService;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplateImpl;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl extends ErrorHandlerTemplateImpl implements UserService {

    private final UserRepository userRepository;
    private final List<PasswordStrategy> validators;
    private final Converter converter;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           List<PasswordStrategy> validators, Converter converter) {

        this.userRepository = userRepository;
        this.validators = validators;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findAllUsers(Pageable pageable) {

        return handleError(() -> userRepository.findAll(pageable),
                "Error while trying to fetch all users: ")
                .map(user -> converter.toResponse(user, UserResponse.class));
    }

    @Transactional(readOnly = true)
    public UserResponse findUserResponseById(Long id) {

        return handleError(() -> userRepository.findById(id),
                "Error while trying to fetch the user by ID: " + id)
                .map(user -> converter.toResponse(user, UserResponse.class))
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public UserResponse createUser(UserRequest userRequest) {

        userRequest.setId(null);
        User user = buildUser(userRequest);

        handleError(() -> userRepository.save(user),
                "Error while trying to create the user: ");
        logger.info("User created: {}", user);

        return converter.toResponse(user, UserResponse.class);
    }

    @Transactional(readOnly = false)
    public UserResponse updateUser(UserRequest userRequest) {

        findUserById(userRequest.getId());
        User user = buildUser(userRequest);

        handleError(() -> userRepository.save(user),
                "Error while trying to update the user: ");
        logger.info("User updated: {}", user);

        return converter.toResponse(user, UserResponse.class);
    }

    @Transactional(readOnly = false)
    public UserResponse createAdminOrManagerUser(UserRequest userRequest) {

        findUserById(userRequest.getId());
        User user = buildAdminOrManagerUser(userRequest);

        handleError(() -> userRepository.save(user),
                "Error trying to update user role: ");
        logger.info("User updated: {}", user);

        return converter.toResponse(user, UserResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteUser(Long id) {

        User user = findUserById(id);

        handleError(() -> {
            userRepository.deleteById(id);
            return null;
        }, "Error while trying to delete the user: ");
        logger.warn("User removed: {}", user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> findUserByName(String name, Pageable pageable) {

        return handleError(() -> userRepository.findByName(name, pageable),
                "Error while trying to fetch the user by name: ").
                map(user -> converter.toResponse(user, UserResponse.class));
    }

    public User findUserById(Long id) {

        return handleError(() -> userRepository.findById(id),
                "Error while trying to fetch the user by ID: " + id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id + "."));
    }

    public void saveUserAddress(User user) {

        handleError(() -> {
            userRepository.save(user);
            return null;
        }, "Error while trying to update the user's address: ");
        logger.info("User's address updated: {}", user);
    }

    public void validatePassword(String password) {

        List<String> failures = new ArrayList<>();

        for (PasswordStrategy strategy : validators) {
            if (!strategy.validate(password)) {
                failures.add(strategy.getRequirement());
            }
        }

        if (!failures.isEmpty()) {
            throw new PasswordException("The password must have at least: " + failures + ".");
        }
    }

    public String encodePassword(UserRequest userRequest) {

        String userPassword = userRequest.getPassword();
        return new BCryptPasswordEncoder().encode(userPassword);
    }

    public User buildUser(UserRequest userRequest) {

        userRequest.setUserRole(UserRole.CLIENT);
        validatePassword(userRequest.getPassword());
        String encodedPassword = encodePassword(userRequest);
        userRequest.setPassword(encodedPassword);

        return converter.toEntity(userRequest, User.class);
    }

    public User buildAdminOrManagerUser(UserRequest userRequest) {

        validatePassword(userRequest.getPassword());
        String encodedPassword = encodePassword(userRequest);
        userRequest.setPassword(encodedPassword);

        return converter.toEntity(userRequest, User.class);
    }
}
