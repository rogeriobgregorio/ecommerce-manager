package com.rogeriogregorio.ecommercemanager.util.impl;

import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplate;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConverterImpl implements Converter {

    private final ModelMapper modelMapper;
    private final ErrorHandlerTemplate errorHandler;

    @Autowired
    public ConverterImpl(ModelMapper modelMapper, ErrorHandlerTemplate errorHandler) {
        this.modelMapper = modelMapper;
        this.errorHandler = errorHandler;
    }

    public <E, R> E toEntity(R object, Class<E> entity) {

        return errorHandler.catchException(() -> modelMapper.map(object, entity),
                "Error while trying to convert from response to entity: ");
    }

    public <E, R> R toResponse(E object, Class<R> response) {

        return errorHandler.catchException(() -> modelMapper.map(object, response),
                "Error while trying to convert from entity to response: ");
    }
}
