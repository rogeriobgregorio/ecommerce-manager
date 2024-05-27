package com.rogeriogregorio.ecommercemanager.util;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public interface DataMapper {

    <E, O> E toEntity(O object, Class<E> entity);

    <O, R> R toResponse(O object, Class<R> response);

    <S, T> T transferSkipNull(S source, T target);

    <T> T fromJson(JSONObject jsonObject, Class<T> targetClass);

    <T> T fromHashMap(Map<String, Object> map, Class<T> targetClass);
}
