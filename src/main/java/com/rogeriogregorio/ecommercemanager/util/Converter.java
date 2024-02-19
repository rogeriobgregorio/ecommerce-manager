package com.rogeriogregorio.ecommercemanager.util;

import org.springframework.stereotype.Component;

@Component
public interface Converter {

    <Entity, Response> Entity toEntity(Response object, Class<Entity> targetType);

    <Entity, Response> Response toResponse(Entity object, Class<Response> targetType);
}