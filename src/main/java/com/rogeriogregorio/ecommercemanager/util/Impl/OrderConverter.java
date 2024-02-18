package com.rogeriogregorio.ecommercemanager.util.Impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderConverter implements Converter<OrderRequest, OrderEntity, OrderResponse> {

    private final ModelMapper modelMapper;

    @Autowired
    public OrderConverter(ModelMapper modelMapper) { this.modelMapper = modelMapper; }

    @Override
    public OrderEntity requestToEntity(OrderRequest orderRequest) {
        return modelMapper.map(orderRequest, OrderEntity.class);
    }

    @Override
    public OrderResponse entityToResponse(OrderEntity orderEntity) {
        return modelMapper.map(orderEntity, OrderResponse.class);
    }

    @Override
    public OrderEntity responseToEntity(OrderResponse orderResponse) {
        return modelMapper.map(orderResponse, OrderEntity.class);
    }
}
