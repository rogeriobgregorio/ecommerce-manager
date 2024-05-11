package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductResponse;
import com.rogeriogregorio.ecommercemanager.entities.Category;
import com.rogeriogregorio.ecommercemanager.entities.Product;
import com.rogeriogregorio.ecommercemanager.entities.ProductDiscount;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.ProductRepository;
import com.rogeriogregorio.ecommercemanager.services.CategoryService;
import com.rogeriogregorio.ecommercemanager.services.ProductDiscountService;
import com.rogeriogregorio.ecommercemanager.services.ProductService;
import com.rogeriogregorio.ecommercemanager.util.DataMapper;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductDiscountService productDiscountService;
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private final Logger logger = LogManager.getLogger();

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryService categoryService,
                              ProductDiscountService productDiscountService,
                              ErrorHandler errorHandler, DataMapper dataMapper) {

        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.productDiscountService = productDiscountService;
        this.errorHandler = errorHandler;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAllProducts(Pageable pageable) {

        return errorHandler.catchException(() -> productRepository.findAll(pageable),
                        "Error while trying to fetch all products: ")
                .map(product -> dataMapper.toResponse(product, ProductResponse.class));
    }

    @Transactional(readOnly = false)
    public ProductResponse createProduct(ProductRequest productRequest) {

        productRequest.setId(null);
        Product product = buildCreateProduct(productRequest);

        errorHandler.catchException(() -> productRepository.save(product),
                "Error while trying to create the product: ");
        logger.info("Product created: {}", product);

        return dataMapper.toResponse(product, ProductResponse.class);
    }

    @Transactional(readOnly = true)
    public ProductResponse findProductResponseById(Long id) {

        return errorHandler.catchException(() -> productRepository.findById(id),
                        "Error while trying to create the product: ")
                .map(product -> dataMapper.toResponse(product, ProductResponse.class))
                .orElseThrow(() -> new NotFoundException("Product response not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public ProductResponse updateProduct(ProductRequest productRequest) {

        Product product = buildUpdateProduct(productRequest);

        errorHandler.catchException(() -> productRepository.save(product),
                "Error while trying to update the product: ");
        logger.info("Product updated: {}", product);

        return dataMapper.toResponse(product, ProductResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteProduct(Long id) {

        isProductExists(id);

        errorHandler.catchException(() -> {
            productRepository.deleteById(id);
            return null;
        }, "Error while trying to delete the product: ");
        logger.warn("Product removed: {}", id);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findProductByName(String name, Pageable pageable) {

        return errorHandler.catchException(() -> productRepository.findByName(name, pageable),
                        "Error while trying to fetch the product by name: ")
                .map(product -> dataMapper.toResponse(product, ProductResponse.class));
    }

    public Product findProductById(Long id) {

        return errorHandler.catchException(() -> productRepository.findById(id),
                        "Error while trying to fetch the product by ID: ")
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + id + "."));
    }

    private void isProductExists(Long id) {

        boolean isProductExists = errorHandler.catchException(() -> productRepository.existsById(id),
                "Error while trying to check the presence of the product: ");

        if (!isProductExists) {
            throw new NotFoundException("Product not found with ID: " + id + ".");
        }
    }

    private Product buildCreateProduct(ProductRequest productRequest) {

        Product product = dataMapper.toEntity(productRequest, Product.class);

        List<Long> categoryIdList = productRequest.getCategoryIdList();
        List<Category> categoryList = categoryService.findAllCategoriesByIds(categoryIdList);

        product.getCategories().addAll(categoryList);

        return product;
    }

    private Product buildUpdateProduct(ProductRequest productRequest) {

        isProductExists(productRequest.getId());
        Product product = dataMapper.toEntity(productRequest, Product.class);

        Long discountId = productRequest.getDiscountId();
        ProductDiscount discount = null;

        if (discountId != null) {
            discount = productDiscountService.findProductDiscountById(discountId);
        }

        List<Long> categoryIdList = productRequest.getCategoryIdList();
        List<Category> categoryList = categoryService.findAllCategoriesByIds(categoryIdList);

        product.getCategories().addAll(categoryList);
        product.setProductDiscount(discount);

        return product;
    }
}
