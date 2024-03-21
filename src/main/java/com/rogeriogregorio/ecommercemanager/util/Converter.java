package com.rogeriogregorio.ecommercemanager.util;

import org.springframework.stereotype.Component;

@Component
public interface Converter {

    <E, R> E toEntity(R object, Class<E> targetType);

    <E, R> R toResponse(E object, Class<R> targetType);
}