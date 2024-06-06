package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.CategoryRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.CategoryResponse;
import com.rogeriogregorio.ecommercemanager.entities.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public interface CategoryService {

    Page<CategoryResponse> findAllCategories(Pageable pageable);

    CategoryResponse createCategory(CategoryRequest categoryRequest);

    CategoryResponse findCategoryById(Long id);

    List<Category> findAllCategoriesByIds(List<Long> id);

    CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest);

    void deleteCategory(Long id);

    Page<CategoryResponse> findCategoryByName(String name, Pageable pageable);
}
