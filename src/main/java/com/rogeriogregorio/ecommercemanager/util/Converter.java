package com.rogeriogregorio.ecommercemanager.util;

import org.springframework.stereotype.Component;

@Component
public interface Converter {

    public <E, R> E toEntity(R response, Class<E> entity);

    public <E, R> R toResponse(E entity, Class<R> response);
}
