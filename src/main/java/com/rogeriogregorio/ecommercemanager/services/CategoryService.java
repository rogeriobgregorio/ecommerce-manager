package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.CategoryRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.CategoryResponse;
import com.rogeriogregorio.ecommercemanager.entities.Category;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface CategoryService {

    List<CategoryResponse> findAllCategories();

    CategoryResponse createCategory(CategoryRequest categoryRequest);

    CategoryResponse findCategoryResponseById(Long id);

    Category findCategoryById(Long id);

    List<Category> findAllCategoriesByIds(List<Long> id);

    CategoryResponse updateCategory(CategoryRequest categoryRequest);

    void deleteCategory(Long id);

    List<CategoryResponse> findCategoryByName(String name);
}
