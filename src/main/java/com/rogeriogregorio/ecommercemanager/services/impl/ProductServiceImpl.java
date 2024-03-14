package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductResponse;
import com.rogeriogregorio.ecommercemanager.entities.CategoryEntity;
import com.rogeriogregorio.ecommercemanager.entities.ProductEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.exceptions.ResourceAlreadyExistsException;
import com.rogeriogregorio.ecommercemanager.repositories.ProductRepository;
import com.rogeriogregorio.ecommercemanager.services.CategoryService;
import com.rogeriogregorio.ecommercemanager.services.ProductService;
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
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final Converter converter;
    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryService categoryService, Converter converter) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAllProducts() {

        try {
            return productRepository
                    .findAll()
                    .stream()
                    .map(productEntity -> converter.toResponse(productEntity, ProductResponse.class))
                    .collect(Collectors.toList());

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar todos os produtos: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar todos os produtos: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public ProductResponse createProduct(ProductRequest productRequest) {

        if (isProductExisting(productRequest)) {
            logger.info("O produto já existe: {}", productRequest.toString());
            throw new ResourceAlreadyExistsException("O produto que você está tentando criar já existe: " + productRequest);
        }

        productRequest.setId(null);

        List<CategoryEntity> categoryList = categoryService.findAllCategoriesById(productRequest.getCategoryIdList());

        ProductEntity productEntity = converter.toEntity(productRequest, ProductEntity.class);
        productEntity.getCategories().addAll(categoryList);

        try {
            productRepository.save(productEntity);
            logger.info("Produto criado: {}", productEntity.toString());
            return converter.toResponse(productEntity, ProductResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar criar o produto: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar o produto: " + exception);
        }
    }

    @Transactional(readOnly = true)
    public ProductResponse findProductById(Long id) {

        return productRepository
                .findById(id)
                .map(productEntity -> converter.toResponse(productEntity, ProductResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Produto não encontrado com o ID: {}", id);
                    return new NotFoundException("Produto não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = true)
    public ProductEntity findProductEntityById(Long id) {

        return productRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.warn("Produto não encontrado com o ID: {}", id);
                    return new NotFoundException("Produto não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public ProductResponse updateProduct(ProductRequest productRequest) {

        findProductEntityById(productRequest.getId());

        List<CategoryEntity> categoryList = categoryService.findAllCategoriesById(productRequest.getCategoryIdList());

        ProductEntity productEntity = converter.toEntity(productRequest, ProductEntity.class);
        productEntity.getCategories().addAll(categoryList);

        try {
            productRepository.save(productEntity);
            logger.info("produto atualizado: {}", productEntity.toString());
            return converter.toResponse(productEntity, ProductResponse.class);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar atualizar o produto: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar o produto: " + exception);
        }
    }

    @Transactional(readOnly = false)
    public void deleteProduct(Long id) {

        findProductEntityById(id);

        try {
            productRepository.deleteById(id);
            logger.warn("Produto removido: {}", id);

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar excluir o produto: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir o produto: " + exception);
        }
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findProductByName(String name) {

        try {
            return productRepository
                    .findProductByName(name)
                    .stream()
                    .map(productEntity -> converter.toResponse(productEntity, ProductResponse.class))
                    .collect(Collectors.toList());

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar buscar produto pelo nome: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar produto pelo nome: " + exception);
        }
    }

    @Transactional(readOnly = true)
    public boolean isProductExisting(ProductRequest productRequest) {
        try {
            return productRepository.existsByName(productRequest.getName()) != null;

        } catch (PersistenceException exception) {
            logger.error("Erro ao tentar verificar a existência do produto: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar verificar a existência do produto: " + exception);
        }
    }
}
