package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.CategoryRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.CategoryResponse;
import com.rogeriogregorio.ecommercemanager.entities.CategoryEntity;
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
import java.util.stream.Collectors;

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
                    .map(CategoryEntity -> converter.toResponse(CategoryEntity, CategoryResponse.class))
                    .collect(Collectors.toList());

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar categorias: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar categorias: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {

        if (isExists(categoryRequest)) {
            logger.info("A categoria já existe: {}", categoryRequest.toString());
            return converter.toResponse(categoryRequest, CategoryResponse.class);
        }

        categoryRequest.setId(null);

        CategoryEntity categoryEntity = converter.toEntity(categoryRequest, CategoryEntity.class);

        try {
            categoryRepository.save(categoryEntity);
            logger.info("Categoria criada: {}", categoryEntity.toString());
            return converter.toResponse(categoryEntity, CategoryResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar criar a categoria: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar a categoria: " + exception);
        }
    }

    @Transactional(readOnly = true)
    public CategoryResponse findCategoryById(Long id) {

        return categoryRepository
                .findById(id)
                .map(CategoryEntity -> converter.toResponse(CategoryEntity, CategoryResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Categoria não encontrado com o ID: {}", id);
                    return new NotFoundException("Categoria não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = true)
    public List<CategoryEntity> findAllCategoryById(List<Long> id) {

        try {
            return categoryRepository.findAllById(id);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar categorias: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar categorias: " + exception);
        }

    }

    @Transactional(readOnly = false)
    public CategoryResponse updateCategory(CategoryRequest categoryRequest) {

        findCategoryById(categoryRequest.getId());

        CategoryEntity categoryEntity = converter.toEntity(categoryRequest, CategoryEntity.class);

        try {
            categoryRepository.save(categoryEntity);
            logger.info("Categoria atualizada: {}", categoryEntity.toString());
            return converter.toResponse(categoryEntity, CategoryResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar atualizar a categoria: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar a categoria: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public void deleteCategory(Long id) {

        findCategoryById(id);

        try {
            categoryRepository.deleteById(id);
            logger.warn("Categoria removida: {}", id);

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
                    .map(categoryEntity -> converter.toResponse(categoryEntity, CategoryResponse.class))
                    .collect(Collectors.toList());

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar categorias: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar categorias: " + exception);
        }
    }

    @Transactional(readOnly = true)
    public Boolean isExists(CategoryRequest categoryRequest) {

        try {
            return categoryRepository.findByName(categoryRequest.getName()) != null;

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar verificar existência da categoria: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar verificar existência da categoria: " + exception);
        }
    }
}
