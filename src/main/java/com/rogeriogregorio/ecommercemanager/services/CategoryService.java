package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.CategoryRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.CategoryResponse;
import com.rogeriogregorio.ecommercemanager.entities.CategoryEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CategoryService {

    public List<CategoryResponse> findAllCategories();

    public CategoryResponse createCategory(CategoryRequest categoryRequest);

    public CategoryResponse findCategoryById(Long id);

    public List<CategoryEntity> findAllCategoryById(List<Long> id);

    public CategoryResponse updateCategory(CategoryRequest categoryRequest);

    public void deleteCategory(Long id);

    public List<CategoryResponse> findCategoryByName(String name);
}
