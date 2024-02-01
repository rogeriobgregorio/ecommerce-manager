package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.CategoryRequest;
import com.rogeriogregorio.ecommercemanager.dto.CategoryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    public List<CategoryResponse> findAllCategories();

    public CategoryResponse createCategory(CategoryRequest categoryRequest);

    public CategoryResponse findCategoryById(Long id);

    public CategoryResponse updateCategory(CategoryRequest categoryRequest);

    public void deleteCategory(Long id);
}
