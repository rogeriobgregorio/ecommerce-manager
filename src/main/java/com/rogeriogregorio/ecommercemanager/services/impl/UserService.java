package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.UserResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserService {

    public List<UserResponse> findAllUsers();

    public UserResponse createUser(UserRequest userRequest);

    public UserResponse findUserById(Long id);

    public UserResponse updateUser(UserRequest userRequest);

    public void deleteUser(Long id);
}
