package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserService {

    List<UserResponse> findAllUsers();

    UserResponse createUser(UserRequest userRequest);

    UserResponse findUserResponseById(Long id);

    User findUserById(Long id);

    UserResponse updateUser(UserRequest userRequest);

    void deleteUser(Long id);

    List<UserResponse> findUserByName(String name);

    void saveUserAddress(User user);
}
