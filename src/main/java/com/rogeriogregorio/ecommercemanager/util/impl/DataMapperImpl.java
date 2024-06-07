package com.rogeriogregorio.ecommercemanager.util.impl;

import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
import com.rogeriogregorio.ecommercemanager.util.DataMapper;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataMapperImpl implements DataMapper {

    private final ModelMapper modelMapper;
    private final ErrorHandler errorHandler;

    @Autowired
    public DataMapperImpl(ModelMapper modelMapper, ErrorHandler errorHandler) {
        this.modelMapper = modelMapper;
        this.errorHandler = errorHandler;
    }

    @Override
    public <S, T> T toEntity(S object, Class<T> entity) {

        return errorHandler.catchException(() -> modelMapper.map(object, entity),
                "Error while trying to convert from response to entity: ");
    }

    @Override
    public <S, T> T toResponse(S object, Class<T> response) {

        return errorHandler.catchException(() -> modelMapper.map(object, response),
                "Error while trying to convert from entity to response: ");
    }

    @Override
    public <S, T> T copyTo(S source, T target) {

        return errorHandler.catchException(() -> {
            modelMapper.map(source, target);
            return target;
        }, "Error while trying to transfer data between objects: ");
    }

    @Override
    public <T> T fromJson(JSONObject jsonObject, Class<T> targetClass) {

        return errorHandler.catchException(() -> {
            Map<String, Object> map = jsonObject.toMap();
            return modelMapper.map(map, targetClass);
        }, "Error while trying to convert from JSONObject to object: ");
    }

    @Override
    public <T> T fromMap(Map<String, Object> map, Class<T> targetClass) {

        return errorHandler.catchException(() -> modelMapper.map(map, targetClass),
                "Error while trying to convert from HashMap to object: ");
    }
}
