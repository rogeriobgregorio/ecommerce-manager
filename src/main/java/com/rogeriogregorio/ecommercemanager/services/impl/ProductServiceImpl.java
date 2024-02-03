package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.ProductRequest;
import com.rogeriogregorio.ecommercemanager.dto.ProductResponse;
import com.rogeriogregorio.ecommercemanager.entities.ProductEntity;
import com.rogeriogregorio.ecommercemanager.exceptions.product.ProductNotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.product.ProductRepositoryException;
import com.rogeriogregorio.ecommercemanager.exceptions.user.UserNotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.ProductRepository;
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
    private final Converter<ProductRequest, ProductEntity, ProductResponse> productConverter;
    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, Converter<ProductRequest, ProductEntity, ProductResponse> productConverter) {
        this.productRepository = productRepository;
        this.productConverter = productConverter;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAllProducts() {

        try {
            return productRepository
                    .findAll()
                    .stream()
                    .map(productConverter::entityToResponse)
                    .collect(Collectors.toList());

        } catch (Exception exception) {
            logger.error("Erro ao tentar buscar produtos: {}", exception.getMessage(), exception);
            throw new ProductRepositoryException("Erro ao tentar buscar produtos.", exception);
        }
    }

    @Transactional(readOnly = false)
    public ProductResponse createProduct(ProductRequest productRequest) {

        productRequest.setId(null);

        ProductEntity productEntity = productConverter.requestToEntity(productRequest);

        try {
            productRepository.save(productEntity);
            logger.info("Produto criado: {}", productEntity.toString());

        } catch (Exception exception) {
            logger.error("Erro ao tentar criar o produto: {}", exception.getMessage(), exception);
            throw new ProductRepositoryException("Erro ao tentar criar o produto.", exception);
        }

        return productConverter.entityToResponse(productEntity);
    }

    @Transactional(readOnly = true)
    public ProductResponse findProductById(Long id) {

        return productRepository
                .findById(id)
                .map(productConverter::entityToResponse)
                .orElseThrow(() -> {
                    logger.warn("Produto não encontrado com o ID: {}", id);
                    return new UserNotFoundException("Produto não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public ProductResponse updateProduct(ProductRequest productRequest) {

        ProductEntity productEntity = productConverter.requestToEntity(productRequest);

        productRepository.findById(productEntity.getId()).orElseThrow(() -> {
            logger.warn("Produto não encontrado com o ID: {}", productEntity.getId());
            return new ProductNotFoundException("Produto não encontrado com o ID: " + productEntity.getId() + ".");
        });

        try {
            productRepository.save(productEntity);
            logger.info("produto atualizado: {}", productEntity.toString());

        } catch (Exception exception) {
            logger.error("Erro ao tentar atualizar o produto: {}", exception.getMessage(), exception);
            throw new ProductRepositoryException("Erro ao tentar atualizar o produto.", exception);
        }

        return productConverter.entityToResponse(productEntity);
    }

    @Transactional(readOnly = false)
    public void deleteProduct(Long id) {

        productRepository.findById(id).orElseThrow(() -> {
            logger.warn("Produto não encontrado com o ID: {}", id);
            return new ProductNotFoundException("Produto não encontrado com o ID: " + id + ".");
        });

        try {
            productRepository.deleteById(id);
            logger.warn("Produto removido: {}", id);

        } catch (Exception exception) {
            logger.error("Erro ao tentar excluir o produto: {}", exception.getMessage(), exception);
            throw new ProductRepositoryException("Erro ao tentar excluir o produto.", exception);
        }
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findProductByName(String name) {

        List<ProductEntity> products;

        try {
            products = productRepository.findByName(name);

        } catch (Exception exception) {
            logger.error("Erro ao tentar buscar produtos: {}", exception.getMessage(), exception);
            throw new ProductRepositoryException("Erro ao tentar buscar produtos.", exception);
        }

        if (products.isEmpty()) {
            logger.warn("Nenhum produto encontrado com o nome: {}", name);
            throw new ProductNotFoundException("Nenhum produto com nome " + name + " encontrado.");
        }

        return products.stream()
                .map(productConverter::entityToResponse)
                .collect(Collectors.toList());
    }
}
