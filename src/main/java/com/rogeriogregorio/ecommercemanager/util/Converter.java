package com.rogeriogregorio.ecommercemanager.util;

import org.springframework.stereotype.Component;

@Component
public interface Converter {

    public <E, R> E toEntity(R object, Class<E> entity);

    public <E, R> R toResponse(E object, Class<R> response);
}
