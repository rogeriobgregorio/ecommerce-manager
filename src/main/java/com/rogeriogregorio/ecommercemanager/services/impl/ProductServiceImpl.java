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
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import com.rogeriogregorio.ecommercemanager.utils.catchError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductDiscountService productDiscountService;
    private final catchError catchError;
    private final DataMapper dataMapper;
    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryService categoryService,
                              ProductDiscountService productDiscountService,
                              catchError catchError, DataMapper dataMapper) {

        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.productDiscountService = productDiscountService;
        this.catchError = catchError;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findAllProducts(Pageable pageable) {

        return catchError.run(
                () -> productRepository.findAll(pageable)
                        .map(product -> dataMapper.map(product, ProductResponse.class)),
                "Error while trying to fetch all products: "
        );
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest productRequest) {

        Product product = dataMapper.map(productRequest, Product.class);
        product.setProductDiscount(validateDiscount(productRequest));
        product.setCategories(validateCategory(productRequest));

        Product savedProduct = catchError.run(
                () -> productRepository.save(product),
                "Error while trying to create the product: "
        );

        logger.info("Product created: {}", savedProduct);
        return dataMapper.map(savedProduct, ProductResponse.class);
    }

    @Transactional(readOnly = true)
    public ProductResponse findProductById(Long id) {

        return catchError.run(
                () -> productRepository.findById(id)
                        .map(product -> dataMapper.map(product, ProductResponse.class))
                        .orElseThrow(() -> new NotFoundException("Product response not found with ID: " + id + ".")),
                "Error while trying to find the product with id: "
        );
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {

        Product currentProduct = getProductIfExists(id);
        dataMapper.map(productRequest, currentProduct);
        currentProduct.setProductDiscount(validateDiscount(productRequest));
        currentProduct.setCategories(validateCategory(productRequest));

        Product updatedProduct = catchError.run(
                () -> productRepository.save(currentProduct),
                "Error while trying to update the product: "
        );

        logger.info("Product updated: {}", updatedProduct);
        return dataMapper.map(updatedProduct, ProductResponse.class);
    }

    @Transactional
    public void deleteProduct(Long id) {

        Product product = getProductIfExists(id);

        catchError.run(() -> {
            productRepository.delete(product);
            return null;
        }, "Error while trying to delete the product: ");

        logger.warn("Product deleted: {}", product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findProductByName(String name, Pageable pageable) {

        return catchError.run(
                () -> productRepository.findByName(name, pageable)
                        .map(product -> dataMapper.map(product, ProductResponse.class)),
                "Error while trying to fetch the product by name: "
        );
    }

    public Product getProductIfExists(Long id) {

        return catchError.run(
                () -> productRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Product response not found with ID: " + id + ".")),
                "Error while trying to verify the existence of the product by ID: "
        );
    }

    private Set<Category> validateCategory(ProductRequest productRequest) {

        List<Long> categoryIdList = productRequest.getCategoryIdList();
        if (categoryIdList != null) {
            return new HashSet<>(categoryService.findAllCategoriesByIds(categoryIdList));
        }

        return Collections.emptySet();
    }

    private ProductDiscount validateDiscount(ProductRequest productRequest) {

        Long discountId = productRequest.getDiscountId();
        if (discountId != null) {
            return productDiscountService.getProductDiscountIfExists(discountId);
        }

        return null;
    }
}
