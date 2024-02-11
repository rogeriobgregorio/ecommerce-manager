package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.UserResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface UserService {

    public List<UserResponse> findAllUsers();

    public UserResponse createUser(UserRequest userRequest);

    public UserResponse findUserById(Long id);

    public UserResponse updateUser(UserRequest userRequest);

    public void deleteUser(Long id);

   public List<UserResponse> findUserByName(String name);
}
