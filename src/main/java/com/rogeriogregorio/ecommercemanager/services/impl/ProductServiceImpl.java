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
import com.rogeriogregorio.ecommercemanager.utils.ErrorHandler;
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
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private final Logger logger = LogManager.getLogger(ProductServiceImpl.class);

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
                .map(product -> dataMapper.map(product, ProductResponse.class));
    }

    @Transactional(readOnly = false)
    public ProductResponse createProduct(ProductRequest productRequest) {

        Product product = dataMapper.map(productRequest, Product.class);
        product.setProductDiscount(validateDiscount(productRequest));
        product.setCategories(validateCategory(productRequest));

        errorHandler.catchException(() -> productRepository.save(product),
                "Error while trying to create the product: ");
        logger.info("Product created: {}", product);

        return dataMapper.map(product, ProductResponse.class);
    }

    @Transactional(readOnly = true)
    public ProductResponse findProductById(Long id) {

        return errorHandler.catchException(() -> productRepository.findById(id),
                        "Error while trying to create the product: ")
                .map(product -> dataMapper.map(product, ProductResponse.class))
                .orElseThrow(() -> new NotFoundException("Product response not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {

        Product currentProduct = getProductIfExists(id);
        Product updatedProduct = dataMapper.map(productRequest, currentProduct);
        updatedProduct.setProductDiscount(validateDiscount(productRequest));
        updatedProduct.setCategories(validateCategory(productRequest));

        errorHandler.catchException(() -> productRepository.save(updatedProduct),
                "Error while trying to update the product: ");
        logger.info("Product updated: {}", updatedProduct);

        return dataMapper.map(updatedProduct, ProductResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteProduct(Long id) {

        Product product = getProductIfExists(id);

        errorHandler.catchException(() -> {
            productRepository.delete(product);
            return null;
        }, "Error while trying to delete the product: ");
        logger.warn("Product deleted: {}", product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> findProductByName(String name, Pageable pageable) {

        return errorHandler.catchException(() -> productRepository.findByName(name, pageable),
                        "Error while trying to fetch the product by name: ")
                .map(product -> dataMapper.map(product, ProductResponse.class));
    }

    public Product getProductIfExists(Long id) {

        return errorHandler.catchException(() -> {

            if (!productRepository.existsById(id)) {
                throw new NotFoundException("Product not exists with ID: " + id + ".");
            }

            return dataMapper.map(productRepository.findById(id), Product.class);
        }, "Error while trying to verify the existence of the product by ID: ");
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
