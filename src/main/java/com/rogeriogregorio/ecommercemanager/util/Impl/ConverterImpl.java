package com.rogeriogregorio.ecommercemanager.util.Impl;

import com.rogeriogregorio.ecommercemanager.exceptions.ConverterException;
import com.rogeriogregorio.ecommercemanager.services.impl.UserServiceImpl;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.MappingException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConverterImpl implements Converter {

    private final ModelMapper modelMapper;
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    public ConverterImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public <Entity, Response> Entity toEntity(Response object, Class<Entity> targetType) {

        try {
            return modelMapper.map(object, targetType);

        } catch (MappingException exception) {
            logger.error("Erro ao tentar converter response para entity: {}", exception.getMessage(), exception);
            throw new ConverterException("Erro ao tentar converter response para entity: " + exception);
        }
    }

    @Override
    public <Entity, Response> Response toResponse(Entity object, Class<Response> targetType) {

        try {
            return modelMapper.map(object, targetType);

        } catch (MappingException exception) {
            logger.error("Erro ao tentar converter entity para response: {}", exception.getMessage(), exception);
            throw new ConverterException("Erro ao tentar converter entity para response: " + exception);
        }
    }
}
