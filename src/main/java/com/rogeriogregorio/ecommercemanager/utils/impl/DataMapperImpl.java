package com.rogeriogregorio.ecommercemanager.utils.impl;

import com.rogeriogregorio.ecommercemanager.utils.catchError;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataMapperImpl implements DataMapper {

    private final ModelMapper modelMapper;
    private final catchError catchError;

    @Autowired
    public DataMapperImpl(ModelMapper modelMapper, catchError catchError) {
        this.modelMapper = modelMapper;
        this.catchError = catchError;
    }

    @Override
    public <S, T> T map(S source, Class<T> targetClass) {

        return catchError.run(
                () -> modelMapper.map(source, targetClass),
                "Error when trying to map data between objects: "
        );
    }

    @Override
    public <S, T> T map(S source, T target) {

        return catchError.run(() -> {
            modelMapper.map(source, target);
            return target;
        }, "Error when trying to map data between objects: ");
    }

    @Override
    public <T> T fromJson(JSONObject jsonObject, Class<T> targetClass) {

        return catchError.run(
                () -> modelMapper.map(jsonObject.toMap(), targetClass),
                "Error while trying to map from JSONObject to object: "
        );
    }

    @Override
    public <T> T fromMap(Map<String, Object> source, Class<T> targetClass) {

        return catchError.run(
                () -> modelMapper.map(source, targetClass),
                "Error while trying to map from HashMap to object: "
        );
    }
}
