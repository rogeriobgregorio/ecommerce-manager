package com.rogeriogregorio.ecommercemanager.util.Impl;

import com.rogeriogregorio.ecommercemanager.dto.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.util.UserConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserConverterImpl implements UserConverter {

    private static final ModelMapper modelMapper = new ModelMapper();

    @Override
    public UserEntity requestToEntity(UserRequest userRequest) {

        return modelMapper.map(userRequest, UserEntity.class);
    }

    @Override
    public UserResponse entityToResponse(UserEntity userEntity) {

        return modelMapper.map(userEntity, UserResponse.class);
    }
}
