package com.rogeriogregorio.ecommercemanager.util;

import org.springframework.stereotype.Component;

@Component
public interface Converter<Request, Entity, Response> {

    public Entity requestToEntity(Request request);

    public Response entityToResponse(Entity entity);

    public Entity responseToEntity(Response response);
}