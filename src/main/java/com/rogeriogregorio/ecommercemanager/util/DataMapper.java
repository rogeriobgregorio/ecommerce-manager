package com.rogeriogregorio.ecommercemanager.util;

import org.springframework.stereotype.Component;

@Component
public interface DataMapper {

    <E, O> E toEntity(O object, Class<E> entity);

    <O, R> R toResponse(O object, Class<R> response);

    <S, T> T transfer(S source, T target);
}
