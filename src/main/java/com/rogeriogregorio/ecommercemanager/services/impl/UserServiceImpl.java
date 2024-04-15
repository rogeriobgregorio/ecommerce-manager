package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.enums.UserRole;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.UserRepository;
import com.rogeriogregorio.ecommercemanager.services.UserService;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplateImpl;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends ErrorHandlerTemplateImpl implements UserService {

    private final UserRepository userRepository;
    private final Converter converter;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           Converter converter) {

        this.userRepository = userRepository;
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
        String role = String.valueOf(user.getUserRole());

        handleError(() -> userRepository.save(user),
                "Error while trying to update the " + role + " user: ");
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

    public User buildUser(UserRequest userRequest) {

        userRequest.setUserRole(UserRole.CLIENT);
        String encryptedPassword = new BCryptPasswordEncoder().encode(userRequest.getPassword());
        userRequest.setPassword(encryptedPassword);

        return converter.toEntity(userRequest, User.class);
    }

    public User buildAdminOrManagerUser(UserRequest userRequest) {

        String encryptedPassword = new BCryptPasswordEncoder().encode(userRequest.getPassword());
        userRequest.setPassword(encryptedPassword);

        return converter.toEntity(userRequest, User.class);
    }
}
