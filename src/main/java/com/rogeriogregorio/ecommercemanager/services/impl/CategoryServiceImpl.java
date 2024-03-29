package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.CategoryRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.CategoryResponse;
import com.rogeriogregorio.ecommercemanager.entities.Category;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.CategoryRepository;
import com.rogeriogregorio.ecommercemanager.services.CategoryService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final Converter converter;
    private static final Logger logger = LogManager.getLogger(CategoryServiceImpl.class);

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, Converter converter) {
        this.categoryRepository = categoryRepository;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAllCategories() {

        try {
            return categoryRepository
                    .findAll()
                    .stream()
                    .map(category -> converter.toResponse(category, CategoryResponse.class))
                    .toList();

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar todas as categorias: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar todas as categorias: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {

        categoryRequest.setId(null);

        Category category = buildCategory(categoryRequest);

        try {
            categoryRepository.save(category);
            logger.info("Categoria criada: {}", category);
            return converter.toResponse(category, CategoryResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar criar a categoria: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar a categoria: " + exception);
        }
    }

    @Transactional(readOnly = true)
    public CategoryResponse findCategoryResponseById(Long id) {

        return categoryRepository
                .findById(id)
                .map(category -> converter.toResponse(category, CategoryResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Categoria não encontrado com o ID: {}", id);
                    return new NotFoundException("Categoria não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = true)
    public List<Category> findAllCategoriesByIds(List<Long> id) {

        try {
            return categoryRepository.findAllById(id);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar todas as categorias por id: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar todas as categorias por id: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public CategoryResponse updateCategory(CategoryRequest categoryRequest) {

        findCategoryById(categoryRequest.getId());

        Category category = buildCategory(categoryRequest);

        try {
            categoryRepository.save(category);
            logger.info("Categoria atualizada: {}", category);
            return converter.toResponse(category, CategoryResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar atualizar a categoria: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar a categoria: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public void deleteCategory(Long id) {

        Category category = findCategoryById(id);

        try {
            categoryRepository.deleteById(id);
            logger.warn("Categoria removida: {}", category);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar excluir a categoria: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir a categoria: " + exception);
        }
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findCategoryByName(String name) {

        try {
            return categoryRepository
                    .findCategoryByName(name)
                    .stream()
                    .map(category -> converter.toResponse(category, CategoryResponse.class))
                    .toList();

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar categoria pelo nome: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar categoria pelo nome: " + exception);
        }
    }

    public Category findCategoryById(Long id) {

        return categoryRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.warn("Categoria não encontrado com o ID: {}", id);
                    return new NotFoundException("Categoria não encontrado com o ID: " + id + ".");
                });
    }

    public Category buildCategory(CategoryRequest categoryRequest) {

        Long id = categoryRequest.getId();
        String name = categoryRequest.getName();

        return (id == null) ? new Category(name) : new Category(id, name);
    }
}
