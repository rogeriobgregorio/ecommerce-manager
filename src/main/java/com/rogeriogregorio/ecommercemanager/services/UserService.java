package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserService {

    List<UserResponse> findAllUsers();

    UserResponse createUser(UserRequest userRequest);

    UserResponse findUserById(Long id);

    UserEntity findUserEntityById(Long id);

    UserResponse updateUser(UserRequest userRequest);

    void deleteUser(Long id);

    List<UserResponse> findUserByName(String name);

    void saveUserAddress(UserEntity userEntity);
}
