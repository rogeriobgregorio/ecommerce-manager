package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.CategoryRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.CategoryResponse;
import com.rogeriogregorio.ecommercemanager.entities.Category;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.CategoryRepository;
import com.rogeriogregorio.ecommercemanager.services.CategoryService;
import com.rogeriogregorio.ecommercemanager.util.DataMapper;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private final Logger logger = LogManager.getLogger(CategoryServiceImpl.class);

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               ErrorHandler errorHandler, DataMapper dataMapper) {

        this.categoryRepository = categoryRepository;
        this.errorHandler = errorHandler;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> findAllCategories(Pageable pageable) {

        return errorHandler.catchException(() -> categoryRepository.findAll(pageable),
                        "Error while trying to fetch all categories: ")
                .map(category -> dataMapper.map(category, CategoryResponse.class));
    }

    @Transactional(readOnly = false)
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {

        Category category = dataMapper.map(categoryRequest, Category.class);

        errorHandler.catchException(() -> categoryRepository.save(category),
                "Error while trying to create the category: ");
        logger.info("Category created: {}", category);

        return dataMapper.map(category, CategoryResponse.class);
    }

    @Transactional(readOnly = true)
    public CategoryResponse findCategoryById(Long id) {

        return errorHandler.catchException(() -> categoryRepository.findById(id),
                        "Error while trying to find the category by ID: ")
                .map(category -> dataMapper.map(category, CategoryResponse.class))
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + id + "."));
    }

    @Transactional(readOnly = true)
    public List<Category> findAllCategoriesByIds(List<Long> id) {

        return errorHandler.catchException(() -> categoryRepository.findAllById(id),
                "Error while trying to fetch all categories by ID: ");
    }

    @Transactional(readOnly = false)
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {

        Category currentCategory = getCategoryIfExists(id);
        Category updatedCategory = dataMapper.map(categoryRequest, currentCategory);

        errorHandler.catchException(() -> categoryRepository.save(updatedCategory),
                "Error while trying to update the category: ");
        logger.info("Category updated: {}", updatedCategory);

        return dataMapper.map(updatedCategory, CategoryResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteCategory(Long id) {

        Category category = getCategoryIfExists(id);

        errorHandler.catchException(() -> {
            categoryRepository.delete(category);
            return null;
        }, "Error while trying to delete the category: ");
        logger.warn("Category deleted: {}", category);
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> findCategoryByName(String name, Pageable pageable) {

        return errorHandler.catchException(() -> categoryRepository.findByName(name, pageable),
                        "Error while trying to fetch category by name: ")
                .map(category -> dataMapper.map(category, CategoryResponse.class));
    }

    private Category getCategoryIfExists(Long id) {

        return errorHandler.catchException(() -> {

            if (!categoryRepository.existsById(id)) {
                throw new NotFoundException("Category not exists with ID: " + id + ".");
            }

            return dataMapper.map(categoryRepository.findById(id), Category.class);
        }, "Error while trying to verify the existence of the category by ID: ");
    }
}
