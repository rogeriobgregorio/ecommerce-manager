package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductResponse;
import com.rogeriogregorio.ecommercemanager.entities.Category;
import com.rogeriogregorio.ecommercemanager.entities.Product;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.ProductRepository;
import com.rogeriogregorio.ecommercemanager.services.CategoryService;
import com.rogeriogregorio.ecommercemanager.services.ProductService;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplateImpl;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl extends ErrorHandlerTemplateImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final Converter converter;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryService categoryService,
                              Converter converter) {

        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAllProducts(Pageable pageable) {

        return handleError(() -> productRepository.findAll(pageable),
                "Erro ao tentar buscar todos os produtos: ")
                .map(product -> converter.toResponse(product, ProductResponse.class));
    }

    @Transactional(readOnly = false)
    public ProductResponse createProduct(ProductRequest productRequest) {

        productRequest.setId(null);
        Product product = buildProduct(productRequest);

        handleError(() -> productRepository.save(product),
                "Erro ao tentar criar o produto: ");

        logger.info("Produto criado: {}", product);
        return converter.toResponse(product, ProductResponse.class);
    }

    @Transactional(readOnly = true)
    public ProductResponse findProductResponseById(Long id) {

        return handleError(() -> productRepository.findById(id),
                "Erro ao tentar criar o produto: ")
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

        handleError(() -> productRepository.save(product),
                "Erro ao tentar atualizar o produto: {}");
        logger.info("produto atualizado: {}", product);

        return converter.toResponse(product, ProductResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteProduct(Long id) {

        Product product = findProductById(id);

        handleError(() -> {
            productRepository.deleteById(id);
            return null;
        }, "Erro ao tentar excluir o produto: ");
        logger.warn("Produto removido: {}", product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findProductByName(String name, Pageable pageable) {

        return handleError(() -> productRepository.findProductByName(name, pageable),
                "Erro ao tentar buscar o produto pelo nome: ")
                .map(product -> converter.toResponse(product, ProductResponse.class));
    }

    public Product findProductById(Long id) {

        return handleError(() -> productRepository.findById(id),
                "Erro ao tentar buscar o produto pelo id: ")
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
