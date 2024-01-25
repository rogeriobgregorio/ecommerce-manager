package com.rogeriogregorio.ecommercemanager.util.Impl;

import com.rogeriogregorio.ecommercemanager.dto.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserConverterImpl implements Converter<UserRequest, UserEntity, UserResponse> {

    private final ModelMapper modelMapper;

    @Autowired
    public UserConverterImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserEntity requestToEntity(UserRequest userRequest) {
        return modelMapper.map(userRequest, UserEntity.class);
    }

    @Override
    public UserResponse entityToResponse(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserResponse.class);
    }

    @Override
    public List<UserEntity> listRequestToEntity(List<UserRequest> requestList) {
        return requestList.stream()
                .map(this::requestToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> listEntityToResponse(List<UserEntity> entityList) {
        return entityList.stream()
                .map(this::entityToResponse)
                .collect(Collectors.toList());
    }
}
