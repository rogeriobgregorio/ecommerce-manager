package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.CategoryResponse;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductResponse;
import com.rogeriogregorio.ecommercemanager.entities.CategoryEntity;
import com.rogeriogregorio.ecommercemanager.entities.ProductEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.ProductRepository;
import com.rogeriogregorio.ecommercemanager.services.CategoryService;
import com.rogeriogregorio.ecommercemanager.services.ProductService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
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

        } catch (Exception exception) {
            logger.error("Erro ao tentar buscar produtos: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar produtos.", exception);
        }
    }

    @Transactional(readOnly = false)
    public ProductResponse createProduct(ProductRequest productRequest) {

        productRequest.setId(null);
        ProductEntity productEntity = converter.toEntity(productRequest, ProductEntity.class);

        try {
            CategoryResponse categoryResponse = categoryService.findCategoryById(productRequest.getCategoryId());
            CategoryEntity categoryEntity = converter.toEntity(categoryResponse, CategoryEntity.class);

            productEntity.getCategories().add(categoryEntity);

            productRepository.save(productEntity);
            logger.info("Produto criado: {}", productEntity.toString());

        } catch (NotFoundException exception) {
            throw new NotFoundException("Categoria não encontrado com o ID: " + productRequest.getCategoryId() + ".");

        } catch (Exception exception) {
            logger.error("Erro ao tentar criar o produto: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar criar o produto.", exception);
        }

        return converter.toResponse(productEntity, ProductResponse.class);
    }

    @Transactional(readOnly = false)
    public ProductResponse updateProduct(ProductRequest productRequest) {

        ProductEntity productEntity = converter.toEntity(productRequest, ProductEntity.class);

        productRepository.findById(productEntity.getId()).orElseThrow(() -> {
            logger.warn("Produto não encontrado com o ID: {}", productEntity.getId());
            return new NotFoundException("Produto não encontrado com o ID: " + productEntity.getId() + ".");
        });

        try {
            CategoryResponse categoryResponse = categoryService.findCategoryById(productRequest.getCategoryId());
            CategoryEntity categoryEntity = converter.toEntity(categoryResponse, CategoryEntity.class);

            productEntity.getCategories().add(categoryEntity);

            productRepository.save(productEntity);
            logger.info("produto atualizado: {}", productEntity.toString());

        } catch (NotFoundException exception) {
            throw new NotFoundException("Categoria não encontrado com o ID: " + productRequest.getCategoryId() + ".");

        } catch (Exception exception) {
            logger.error("Erro ao tentar atualizar o produto: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar atualizar o produto.", exception);
        }

        return converter.toResponse(productEntity, ProductResponse.class);
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

    @Transactional(readOnly = false)
    public void deleteProduct(Long id) {

        productRepository.findById(id).orElseThrow(() -> {
            logger.warn("Produto não encontrado com o ID: {}", id);
            return new NotFoundException("Produto não encontrado com o ID: " + id + ".");
        });

        try {
            productRepository.deleteById(id);
            logger.warn("Produto removido: {}", id);

        } catch (Exception exception) {
            logger.error("Erro ao tentar excluir o produto: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar excluir o produto.", exception);
        }
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findProductByName(String name) {

        List<ProductEntity> products;

        try {
            products = productRepository.findByName(name);

        } catch (Exception exception) {
            logger.error("Erro ao tentar buscar produtos: {}", exception.getMessage(), exception);
            throw new RepositoryException("Erro ao tentar buscar produtos.", exception);
        }

        if (products.isEmpty()) {
            logger.warn("Nenhum produto encontrado com o nome: {}", name);
            throw new NotFoundException("Nenhum produto com nome " + name + " encontrado.");
        }

        return products.stream()
                .map(productEntity -> converter.toResponse(productEntity, ProductResponse.class))
                .collect(Collectors.toList());
    }
}
