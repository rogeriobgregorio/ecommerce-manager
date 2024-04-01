package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductResponse;
import com.rogeriogregorio.ecommercemanager.entities.Category;
import com.rogeriogregorio.ecommercemanager.entities.Product;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.exceptions.RepositoryException;
import com.rogeriogregorio.ecommercemanager.repositories.ProductRepository;
import com.rogeriogregorio.ecommercemanager.services.CategoryService;
import com.rogeriogregorio.ecommercemanager.services.ProductService;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import jakarta.persistence.PersistenceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final Converter converter;
    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryService categoryService,
                              Converter converter) {

        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAllProducts(int page, int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> productsPage = productRepository.findAll(pageable);
            return productsPage
                    .map(product -> converter
                    .toResponse(product, ProductResponse.class));

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar buscar todos os produtos: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar buscar todos os produtos: " + ex);
        }
    }

    @Transactional(readOnly = false)
    public ProductResponse createProduct(ProductRequest productRequest) {

        productRequest.setId(null);
        Product product = buildProduct(productRequest);

        try {
            productRepository.save(product);
            logger.info("Produto criado: {}", product);
            return converter.toResponse(product, ProductResponse.class);

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar criar o produto: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar criar o produto: " + ex);
        }
    }

    @Transactional(readOnly = true)
    public ProductResponse findProductResponseById(Long id) {

        return productRepository
                .findById(id)
                .map(product -> converter.toResponse(product, ProductResponse.class))
                .orElseThrow(() -> {
                    logger.warn("Produto não encontrado com o ID: {}", id);
                    return new NotFoundException("Produto não encontrado com o ID: " + id + ".");
                });
    }

    @Transactional(readOnly = false)
    public ProductResponse updateProduct(ProductRequest productRequest) {

        findProductById(productRequest.getId());
        Product product = buildProduct(productRequest);

        try {
            productRepository.save(product);
            logger.info("produto atualizado: {}", product);
            return converter.toResponse(product, ProductResponse.class);

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar atualizar o produto: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar atualizar o produto: " + ex);
        }
    }

    @Transactional(readOnly = false)
    public void deleteProduct(Long id) {

        Product product = findProductById(id);

        try {
            productRepository.deleteById(id);
            logger.warn("Produto removido: {}", product);

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar excluir o produto: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar excluir o produto: " + ex);
        }
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findProductByName(String name, int page, int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> productsPage = productRepository.findProductByName(name, pageable);
            return productsPage
                    .map(product -> converter
                    .toResponse(product, ProductResponse.class));

        } catch (PersistenceException ex) {
            logger.error("Erro ao tentar buscar produto pelo nome: {}", ex.getMessage(), ex);
            throw new RepositoryException("Erro ao tentar buscar produto pelo nome: " + ex);
        }
    }

    public Product findProductById(Long id) {

        return productRepository
                .findById(id)
                .orElseThrow(() -> {
                    logger.warn("Produto não encontrado com o ID: {}", id);
                    return new NotFoundException("Produto não encontrado com o ID: " + id + ".");
                });
    }

    public Product buildProduct(ProductRequest productRequest) {

        List<Long> categoryIdList = productRequest.getCategoryIdList();
        List<Category> categoryList = categoryService.findAllCategoriesByIds(categoryIdList);

        Product product = converter.toEntity(productRequest, Product.class);
        product.getCategories().addAll(categoryList);

        return product;
    }
}
