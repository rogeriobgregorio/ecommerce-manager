package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public interface UserService {

    Page<UserResponse> findAllUsers(int page, int size);

    UserResponse createUser(UserRequest userRequest);

    UserResponse findUserResponseById(Long id);

    User findUserById(Long id);

    UserResponse updateUser(UserRequest userRequest);

    void deleteUser(Long id);

    Page<UserResponse> findUserByName(String name, int page, int size);

    void saveUserAddress(User user);
}
