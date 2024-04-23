package com.rogeriogregorio.ecommercemanager.util.impl;

import com.rogeriogregorio.ecommercemanager.services.ErrorHandlerTemplate;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConverterImpl implements Converter {

    private final ModelMapper mapper;
    private final ErrorHandlerTemplate handler;

    @Autowired
    public ConverterImpl(ModelMapper mapper, ErrorHandlerTemplate handler) {
        this.mapper = mapper;
        this.handler = handler;
    }

    public <E, R> E toEntity(R response, Class<E> entity) {

        return handler.catchException(() -> mapper.map(response, entity),
                "Error while trying to convert from response to entity: ");
    }

    public <E, R> R toResponse(E entity, Class<R> response) {

        return handler.catchException(() -> mapper.map(entity, response),
                "Error while trying to convert from entity to response: ");
    }
}
