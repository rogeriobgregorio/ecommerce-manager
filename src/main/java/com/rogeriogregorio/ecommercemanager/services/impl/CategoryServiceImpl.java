package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.CategoryRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.CategoryResponse;
import com.rogeriogregorio.ecommercemanager.entities.Category;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.CategoryRepository;
import com.rogeriogregorio.ecommercemanager.services.CategoryService;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplateImpl;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl extends ErrorHandlerTemplateImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final Converter converter;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               Converter converter) {

        this.categoryRepository = categoryRepository;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> findAllCategories(Pageable pageable) {

        return handleError(() -> categoryRepository.findAll(pageable),
                "Error while trying to fetch all categories: ")
                .map(category -> converter.toResponse(category, CategoryResponse.class));
    }

    @Transactional(readOnly = false)
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {

        categoryRequest.setId(null);
        Category category = buildCategory(categoryRequest);

        handleError(() -> categoryRepository.save(category),
                "Error while trying to create the category: ");
        logger.info("Category created: {}", category);

        return converter.toResponse(category, CategoryResponse.class);
    }

    @Transactional(readOnly = true)
    public CategoryResponse findCategoryResponseById(Long id) {

        return handleError(() -> categoryRepository.findById(id),
                "Error while trying to find the category by ID: ")
                .map(category -> converter.toResponse(category, CategoryResponse.class))
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + id + "."));
    }

    @Transactional(readOnly = true)
    public List<Category> findAllCategoriesByIds(List<Long> id) {

        return handleError(() -> categoryRepository.findAllById(id),
                "Error while trying to fetch all categories by ID: ");
    }

    @Transactional(readOnly = false)
    public CategoryResponse updateCategory(CategoryRequest categoryRequest) {

        findCategoryById(categoryRequest.getId());
        Category category = buildCategory(categoryRequest);

        handleError(() -> categoryRepository.save(category),
                "Error while trying to update the category: ");
        logger.info("Category updated: {}", category);

        return converter.toResponse(category, CategoryResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteCategory(Long id) {

        Category category = findCategoryById(id);

        handleError(() -> {
            categoryRepository.deleteById(id);
            return null;
        }, "Error while trying to delete the category: ");
        logger.warn("Category removed: {}", category);
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> findCategoryByName(String name, Pageable pageable) {

        return handleError(() -> categoryRepository.findByName(name, pageable),
                "Error while trying to fetch category by name: ")
                .map(category -> converter.toResponse(category, CategoryResponse.class));
    }

    public Category findCategoryById(Long id) {

        return handleError(() -> categoryRepository.findById(id),
                "Error while trying to find the category by ID: ")
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + id + "."));
    }

    public Category buildCategory(CategoryRequest categoryRequest) {

        Long id = categoryRequest.getId();
        String name = categoryRequest.getName();

        return new Category(id, name);
    }
}
