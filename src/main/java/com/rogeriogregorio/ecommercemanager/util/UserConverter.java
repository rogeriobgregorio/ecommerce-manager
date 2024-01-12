package com.rogeriogregorio.ecommercemanager.util;

import com.rogeriogregorio.ecommercemanager.dto.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public interface UserConverter {

    public UserEntity requestToEntity(UserRequest userRequest);

    public UserResponse entityToResponse(UserEntity userEntity);
}