package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface UserService {

    Page<UserResponse> findAllUsers(Pageable pageable);

    UserResponse registerUser(UserRequest userRequest);

    UserResponse createAdminOrManagerUser(UUID id, UserRequest userRequest);

    UserResponse findUserById(UUID id);

    UserResponse updateUser(UUID id, UserRequest userRequest);

    void deleteUser(UUID id);

    User getUserIfExists(UUID id);

    Page<UserResponse> findUserByName(String name, Pageable pageable);

    void saveUserAddress(User user);
}
