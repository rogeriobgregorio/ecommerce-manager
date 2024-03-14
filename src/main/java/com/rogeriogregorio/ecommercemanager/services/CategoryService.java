package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.CategoryRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.CategoryResponse;
import com.rogeriogregorio.ecommercemanager.entities.CategoryEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CategoryService {

    List<CategoryResponse> findAllCategories();

    CategoryResponse createCategory(CategoryRequest categoryRequest);

    CategoryResponse findCategoryById(Long id);

    List<CategoryEntity> findAllCategoriesById(List<Long> id);

    CategoryResponse updateCategory(CategoryRequest categoryRequest);

    void deleteCategory(Long id);

    List<CategoryResponse> findCategoryByName(String name);

    boolean isCategoryExisting(CategoryRequest categoryRequest);
}
