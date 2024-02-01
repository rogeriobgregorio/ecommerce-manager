package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.CategoryRequest;
import com.rogeriogregorio.ecommercemanager.dto.CategoryResponse;
import com.rogeriogregorio.ecommercemanager.entities.CategoryEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.category.CategoryNotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.category.CategoryRepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.CategoryRepository;
import com.rogeriogregorio.ecommercemanager.services.CategoryService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import jakarta.validation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final Converter<CategoryRequest, CategoryEntity, CategoryResponse> categoryConverter;
    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, Converter<CategoryRequest, CategoryEntity, CategoryResponse> categoryConverter) {
        this.categoryRepository = categoryRepository;
        this.categoryConverter = categoryConverter;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findAllCategories() {
        try {
            return categoryRepository
                    .findAll()
                    .stream()
                    .map(categoryConverter::entityToResponse)
                    .collect(Collectors.toList());

        } catch (Exception exception) {
            logger.error("Erro ao tentar buscar categorias: {}", exception.getMessage(), exception);
            throw new CategoryRepositoryException("Erro ao tentar buscar categorias: " + exception.getMessage() + ".");
        }
    }

    @Transactional(readOnly = false)
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        categoryRequest.setId(null);

        CategoryEntity categoryEntity = categoryConverter.requestToEntity(categoryRequest);

        validateUser(categoryEntity);

        try {
            categoryRepository.save(categoryEntity);
            logger.info("Usuário criado: {}", categoryEntity.toString());

        } catch (Exception exception) {
            logger.error("Erro ao tentar criar o categoria: {}", exception.getMessage(), exception);
            throw new CategoryRepositoryException("Erro ao tentar criar o categoria: " + exception.getMessage() + ".");
        }

        return categoryConverter.entityToResponse(categoryEntity);
    }

    @Transactional(readOnly = true)
    public CategoryResponse findCategoryById(Long id) {

        return categoryRepository
                .findById(id)
                .map(categoryConverter::entityToResponse)
                .orElseThrow(() -> {
                    logger.warn("Categoria não encontrado com o ID: {}", id);
                    return new CategoryNotFoundException("Categoria não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public CategoryResponse updateCategory(CategoryRequest categoryRequest) {

        CategoryEntity categoryEntity = categoryConverter.requestToEntity(categoryRequest);

        validateUser(categoryEntity);

        categoryRepository.findById(categoryEntity.getId()).orElseThrow(() -> {
            logger.warn("Usuário não encontrado com o ID: {}", categoryEntity.getId());
            return new CategoryNotFoundException("Usuário não encontrado com o ID: " + categoryEntity.getId() + ".");
        });

        try {
            categoryRepository.save(categoryEntity);
            logger.info("Categoria atualizada: {}", categoryEntity.toString());

        } catch (Exception exception) {
            logger.error("Erro ao tentar atualizar a categoria: {}", exception.getMessage(), exception);
            throw new CategoryRepositoryException("Erro ao tentar atualizar a categoria.", exception);
        }

        return categoryConverter.entityToResponse(categoryEntity);
    }

    @Transactional(readOnly = false)
    public void deleteCategory(Long id) {

        categoryRepository.findById(id).orElseThrow(() -> {
            logger.warn("Categoria não encontrada com o ID: {}", id);
            return new CategoryNotFoundException("Categoria não encontrada com o ID: " + id + ".");
        });

        try {
            categoryRepository.deleteById(id);
            logger.warn("Categoria removida: {}", id);

        } catch (Exception exception) {
            logger.error("Erro ao tentar excluir a categoria: {}", exception.getMessage(), exception);
            throw new CategoryRepositoryException("Erro ao tentar excluir a categoria: " + exception.getMessage() + ".");
        }
    }

    public void validateUser(CategoryEntity categoryEntity) {

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

            Set<ConstraintViolation<CategoryEntity>> violations = validator.validate(categoryEntity);

            if (!violations.isEmpty()) {
                throw new ConstraintViolationException("Falha na validação", violations);
            }
        }
    }
}
