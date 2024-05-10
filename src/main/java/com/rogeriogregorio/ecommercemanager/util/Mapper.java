package com.rogeriogregorio.ecommercemanager.util;

import org.springframework.stereotype.Component;

@Component
public interface Mapper {

    <E, R> E toEntity(R object, Class<E> entity);

    <E, R> R toResponse(E object, Class<R> response);

    <D, S> D transferData(S source, D target);
}
