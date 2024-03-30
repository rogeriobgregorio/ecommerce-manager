package com.rogeriogregorio.ecommercemanager.util.impl;

import com.rogeriogregorio.ecommercemanager.exceptions.ConverterException;
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
    private static final Logger logger = LogManager.getLogger(ConverterImpl.class);

    @Autowired
    public ConverterImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public <E, R> E toEntity(R object, Class<E> targetType) {

        try {
            return modelMapper.map(object, targetType);

        } catch (MappingException exception) {
            logger.error("Erro ao tentar converter de response para entity: {}", exception.getMessage(), exception);
            throw new ConverterException("Erro ao tentar converter de response para entity: " + exception);
        }
    }

    @Override
    public <E, R> R toResponse(E object, Class<R> targetType) {

        try {
            return modelMapper.map(object, targetType);

        } catch (MappingException exception) {
            logger.error("Erro ao tentar converter de entity para response: {}", exception.getMessage(), exception);
            throw new ConverterException("Erro ao tentar converter de entity para response: " + exception);
        }
    }
}
