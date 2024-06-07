package com.rogeriogregorio.ecommercemanager.util;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface DataMapper {

    <S, T> T toEntity(S object, Class<T> entity);

    <S, T> T toResponse(S object, Class<T> response);

    <S, T> T copyTo(S source, T target);

    <T> T fromJson(JSONObject jsonObject, Class<T> targetClass);

    <T> T fromMap(Map<String, Object> map, Class<T> targetClass);
}
