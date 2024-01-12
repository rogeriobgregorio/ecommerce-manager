package com.rogeriogregorio.ecommercemanager.util;

import com.rogeriogregorio.ecommercemanager.dto.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import org.modelmapper.ModelMapper;

public class UserConverter {

    private static final ModelMapper modelMapper = new ModelMapper();

    public UserEntity requestToEntity(UserRequest userRequest) {

        return modelMapper.map(userRequest, UserEntity.class);
    }

    public UserResponse entityToResponse(UserEntity userEntity) {

        return modelMapper.map(userEntity, UserResponse.class);
    }
}
