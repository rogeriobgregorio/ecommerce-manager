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
    public <S, T> T map(S object, Class<T> targetType) {

        return errorHandler.catchException(() -> modelMapper.map(object, targetType),
                "Error when trying to map data between objects: ");
    }

    @Override
    public <S, T> T map(S source, T target) {

        return errorHandler.catchException(() -> {
            modelMapper.map(source, target);
            return target;
        }, "Error when trying to map data between objects: ");
    }

    @Override
    public <T> T fromJson(JSONObject jsonObject, Class<T> targetClass) {

        return errorHandler.catchException(() -> {
            Map<String, Object> map = jsonObject.toMap();
            return modelMapper.map(map, targetClass);
        }, "Error while trying to map from JSONObject to object: ");
    }

    @Override
    public <T> T fromMap(Map<String, Object> map, Class<T> targetClass) {

        return errorHandler.catchException(() -> modelMapper.map(map, targetClass),
                "Error while trying to map from HashMap to object: ");
    }
}
