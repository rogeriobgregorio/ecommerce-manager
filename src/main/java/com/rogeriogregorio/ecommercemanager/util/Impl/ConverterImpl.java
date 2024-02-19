package com.rogeriogregorio.ecommercemanager.util.Impl;

import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConverterImpl implements Converter {

    private final ModelMapper modelMapper;

    @Autowired
    public ConverterImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public <Entity, Response> Entity toEntity(Response object, Class<Entity> targetType) {
        return modelMapper.map(object, targetType);
    }

    @Override
    public <Entity, Response> Response toResponse(Entity object, Class<Response> targetType) {
        return modelMapper.map(object, targetType);
    }
}
