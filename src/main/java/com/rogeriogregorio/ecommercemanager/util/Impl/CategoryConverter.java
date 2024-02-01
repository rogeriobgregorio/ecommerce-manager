package com.rogeriogregorio.ecommercemanager.util.Impl;

import com.rogeriogregorio.ecommercemanager.dto.CategoryRequest;
import com.rogeriogregorio.ecommercemanager.dto.CategoryResponse;
import com.rogeriogregorio.ecommercemanager.entities.CategoryEntity;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter implements Converter<CategoryRequest, CategoryEntity, CategoryResponse> {

    private final ModelMapper modelMapper;

    @Autowired
    public CategoryConverter(ModelMapper modelMapper) { this.modelMapper = modelMapper; }

    @Override
    public CategoryEntity requestToEntity(CategoryRequest categoryRequest) {
        return modelMapper.map(categoryRequest, CategoryEntity.class);
    }

    @Override
    public CategoryResponse entityToResponse(CategoryEntity categoryEntity) {
        return modelMapper.map(categoryEntity, CategoryResponse.class);
    }
}
