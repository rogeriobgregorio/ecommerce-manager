package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.CategoryRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.CategoryResponse;
import com.rogeriogregorio.ecommercemanager.entities.Category;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.CategoryRepository;
import com.rogeriogregorio.ecommercemanager.services.CategoryService;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import com.rogeriogregorio.ecommercemanager.utils.catchError;
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
    private final catchError catchError;
    private final DataMapper dataMapper;
    private static final Logger logger = LogManager.getLogger(CategoryServiceImpl.class);

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               catchError catchError,
                               DataMapper dataMapper) {

        this.categoryRepository = categoryRepository;
        this.catchError = catchError;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> findAllCategories(Pageable pageable) {

        return catchError.run(() -> categoryRepository.findAll(pageable)
                .map(category -> dataMapper.map(category, CategoryResponse.class)));
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {

        Category category = dataMapper.map(categoryRequest, Category.class);

        Category savedCategory = catchError.run(() -> categoryRepository.save(category));
        logger.info("Category created: {}", savedCategory);
        return dataMapper.map(savedCategory, CategoryResponse.class);
    }

    @Transactional(readOnly = true)
    public CategoryResponse findCategoryById(Long id) {

        return catchError.run(() -> categoryRepository.findById(id)
                .map(category -> dataMapper.map(category, CategoryResponse.class))
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + id + ".")));
    }

    @Transactional(readOnly = true)
    public List<Category> findAllCategoriesByIds(List<Long> id) {

        return catchError.run(() -> categoryRepository.findAllById(id));
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {

        Category currentCategory = getCategoryIfExists(id);
        dataMapper.map(categoryRequest, currentCategory);

        Category updatedCategory = catchError.run(() -> categoryRepository.save(currentCategory));
        logger.info("Category updated: {}", updatedCategory);
        return dataMapper.map(updatedCategory, CategoryResponse.class);
    }

    @Transactional
    public void deleteCategory(Long id) {

        Category category = getCategoryIfExists(id);

        catchError.run(() -> categoryRepository.delete(category));
        logger.warn("Category deleted: {}", category);
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> findCategoryByName(String name, Pageable pageable) {

        return catchError.run(() -> categoryRepository.findByName(name, pageable)
                .map(category -> dataMapper.map(category, CategoryResponse.class)));
    }

    private Category getCategoryIfExists(Long id) {

        return catchError.run(() -> categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + id + ".")));
    }
}
