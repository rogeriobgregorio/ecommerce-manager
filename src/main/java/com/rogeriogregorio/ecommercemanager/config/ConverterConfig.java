package com.rogeriogregorio.ecommercemanager.config;

import com.rogeriogregorio.ecommercemanager.dto.UserRequest;
import com.rogeriogregorio.ecommercemanager.dto.UserResponse;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import com.rogeriogregorio.ecommercemanager.util.Impl.UserConverterImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConverterConfig {

    @Autowired
    private ModelMapper modelMapper;

    @Bean
    public Converter<UserRequest, UserEntity, UserResponse> userConverter() {
        return new UserConverterImpl(modelMapper);
    }
}
