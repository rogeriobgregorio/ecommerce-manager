package com.rogeriogregorio.ecommercemanager.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface Converter<Request, Entity, Response> {

    public Entity requestToEntity(Request request);

    public Response entityToResponse(Entity entity);

    List<Entity> listRequestToEntity(List<Request> requestList);

    List<Response> listEntityToResponse(List<Entity> entityList);
}