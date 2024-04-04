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
                "Erro ao tentar buscar todas as categorias: ")
                .map(category -> converter.toResponse(category, CategoryResponse.class));
    }

    @Transactional(readOnly = false)
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {

        categoryRequest.setId(null);
        Category category = buildCategory(categoryRequest);

        handleError(() -> categoryRepository.save(category),
                "Erro ao tentar criar a categoria: ");
        logger.info("Categoria criada: {}", category);

        return converter.toResponse(category, CategoryResponse.class);
    }

    @Transactional(readOnly = true)
    public CategoryResponse findCategoryResponseById(Long id) {

        return handleError(() -> categoryRepository.findById(id),
                "Erro ao tentar encontrar a categoria pelo ID: ")
                .map(category -> converter.toResponse(category, CategoryResponse.class))
                .orElseThrow(() -> new NotFoundException("Categoria não encontrado com o ID: " + id + "."));
    }

    @Transactional(readOnly = true)
    public List<Category> findAllCategoriesByIds(List<Long> id) {

        return handleError(() -> categoryRepository.findAllById(id),
                "Erro ao tentar buscar todas as categorias por id: ");
    }

    @Transactional(readOnly = false)
    public CategoryResponse updateCategory(CategoryRequest categoryRequest) {

        findCategoryById(categoryRequest.getId());
        Category category = buildCategory(categoryRequest);

        handleError(() -> categoryRepository.save(category),
                "Erro ao tentar atualizar a categoria: ");
        logger.info("Categoria atualizada: {}", category);

        return converter.toResponse(category, CategoryResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteCategory(Long id) {

        Category category = findCategoryById(id);

        handleError(() -> {
            categoryRepository.deleteById(id);
            return null;
        }, "Erro ao tentar excluir a categoria: ");
        logger.warn("Categoria removida: {}", category);
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> findCategoryByName(String name, Pageable pageable) {

        return handleError(() -> categoryRepository.findCategoryByName(name, pageable),
                "Erro ao tentar buscar categoria pelo nome: ")
                .map(category -> converter.toResponse(category, CategoryResponse.class));
    }

    public Category findCategoryById(Long id) {

        return handleError(() -> categoryRepository.findById(id),
                "Erro ao tentar encontrar a categoria pelo ID: ")
                .orElseThrow(() -> new NotFoundException("Categoria não encontrado com o ID: " + id + "."));
    }

    public Category buildCategory(CategoryRequest categoryRequest) {

        Long id = categoryRequest.getId();
        String name = categoryRequest.getName();

        return new Category(id, name);
    }
}
