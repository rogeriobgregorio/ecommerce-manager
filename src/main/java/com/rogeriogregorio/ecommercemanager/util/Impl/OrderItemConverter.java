package com.rogeriogregorio.ecommercemanager.util.Impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderItemResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderItemEntity;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderItemConverter implements Converter<OrderItemRequest, OrderItemEntity, OrderItemResponse> {

    private final ModelMapper modelMapper;

    @Autowired
    public OrderItemConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public OrderItemEntity requestToEntity(OrderItemRequest orderItemRequest) {
        return modelMapper.map(orderItemRequest, OrderItemEntity.class);
    }

    @Override
    public OrderItemResponse entityToResponse(OrderItemEntity orderItemEntity) {
        return modelMapper.map(orderItemEntity, OrderItemResponse.class);
    }
}
