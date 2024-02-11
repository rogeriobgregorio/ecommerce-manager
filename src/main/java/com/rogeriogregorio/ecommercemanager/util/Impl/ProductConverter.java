package com.rogeriogregorio.ecommercemanager.util.Impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductResponse;
import com.rogeriogregorio.ecommercemanager.entities.ProductEntity;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter implements Converter<ProductRequest, ProductEntity, ProductResponse> {

    private final ModelMapper modelMapper;

    @Autowired
    public ProductConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductEntity requestToEntity(ProductRequest productRequest) {
        return modelMapper.map(productRequest, ProductEntity.class);
    }

    @Override
    public ProductResponse entityToResponse(ProductEntity productEntity) {
        return modelMapper.map(productEntity, ProductResponse.class);
    }
}
