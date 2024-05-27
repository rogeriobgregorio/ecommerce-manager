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

    public <E, O> E toEntity(O object, Class<E> entity) {

        return errorHandler.catchException(() -> modelMapper.map(object, entity),
                "Error while trying to convert from response to entity: ");
    }

    public <O, R> R toResponse(O object, Class<R> response) {

        return errorHandler.catchException(() -> modelMapper.map(object, response),
                "Error while trying to convert from entity to response: ");
    }

    public <S, T> T transferSkipNull(S source, T target) {

        modelMapper.getConfiguration().setSkipNullEnabled(true);

        return errorHandler.catchException(() -> {
            modelMapper.map(source, target);
            return target;
        }, "Error while trying to transfer data between objects skipping null fields: ");
    }

    public <T> T fromJson(JSONObject jsonObject, Class<T> targetClass) {

        return errorHandler.catchException(() -> {
            Map<String, Object> map = jsonObject.toMap();
            return modelMapper.map(map, targetClass);
        }, "Error while trying to convert from JSONObject to entity: ");
    }

    public <T> T fromHashMap(Map<String, Object> map, Class<T> targetClass) {

        return errorHandler.catchException(() -> modelMapper.map(map, targetClass),
                "Error while trying to convert from HashMap to entity: ");
    }
}
