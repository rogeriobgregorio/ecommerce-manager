package com.rogeriogregorio.ecommercemanager.util.impl;

import com.rogeriogregorio.ecommercemanager.exceptions.ConverterException;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.modelmapper.MappingException;
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
    public <E, R> E toEntity(R object, Class<E> targetType) {

        try {
            return modelMapper.map(object, targetType);

        } catch (MappingException exception) {
            throw new ConverterException("Error while trying to convert from response to entity: " + exception);
        }
    }

    @Override
    public <E, R> R toResponse(E object, Class<R> targetType) {

        try {
            return modelMapper.map(object, targetType);

        } catch (MappingException exception) {
            throw new ConverterException("Error while trying to convert from entity to response: " + exception);
        }
    }
}
