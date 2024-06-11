package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductReviewRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductReviewResponse;
import com.rogeriogregorio.ecommercemanager.entities.Product;
import com.rogeriogregorio.ecommercemanager.entities.ProductReview;
import com.rogeriogregorio.ecommercemanager.entities.User;
import com.rogeriogregorio.ecommercemanager.entities.primarykeys.ProductReviewPK;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.ProductReviewRepository;
import com.rogeriogregorio.ecommercemanager.services.ProductReviewService;
import com.rogeriogregorio.ecommercemanager.services.ProductService;
import com.rogeriogregorio.ecommercemanager.services.UserService;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import com.rogeriogregorio.ecommercemanager.utils.catchError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
public class ProductReviewServiceImpl implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductService productService;
    private final UserService userService;
    private final catchError catchError;
    private final DataMapper dataMapper;
    private static final Logger logger = LogManager.getLogger(ProductReviewServiceImpl.class);

    @Autowired
    public ProductReviewServiceImpl(ProductReviewRepository productReviewRepository,
                                    ProductService productService, UserService userService,
                                    catchError catchError, DataMapper dataMapper) {

        this.productReviewRepository = productReviewRepository;
        this.productService = productService;
        this.userService = userService;
        this.catchError = catchError;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<ProductReviewResponse> findAllProductReviews(Pageable pageable) {

        return catchError.run(() -> productReviewRepository.findAll(pageable)
                .map(productReview -> dataMapper.map(productReview, ProductReviewResponse.class)));
    }

    @Transactional
    public ProductReviewResponse createProductReview(ProductReviewRequest productReviewRequest) {

        ProductReview productReview = buildProductReview(productReviewRequest);

        ProductReview savedProductReview = catchError.run(() -> productReviewRepository.save(productReview));
        logger.info("Product review created: {}", savedProductReview);
        return dataMapper.map(savedProductReview, ProductReviewResponse.class);
    }

    @Transactional(readOnly = true)
    public ProductReviewResponse findProductReviewById(Long productId, UUID userId) {

        ProductReviewPK id = buildProductReviewPK(productId, userId);

        return catchError.run(() -> productReviewRepository.findById(id)
                .map(productReview -> dataMapper.map(productReview, ProductReviewResponse.class))
                .orElseThrow(() -> new NotFoundException("Product review not found with ID: " + id + ".")));
    }

    @Transactional
    public ProductReviewResponse updateProductReview(ProductReviewRequest productReviewRequest) {

        ProductReview productReview = buildProductReview(productReviewRequest);

        ProductReview updateProductReview = catchError.run(() -> productReviewRepository.save(productReview));
        logger.info("Product review updated: {}", updateProductReview);
        return dataMapper.map(updateProductReview, ProductReviewResponse.class);
    }

    @Transactional
    public void deleteProductReview(Long productId, UUID userId) {

        ProductReviewPK id = buildProductReviewPK(productId, userId);

        catchError.run(() -> productReviewRepository.deleteById(id));
        logger.warn("Product review removed: {}", id.getProduct());
    }

    private ProductReview validateProductReview(ProductReview productReview) {

        Set<Product> purchasedProducts = productReview.getUser().getPurchasedProducts();

        if (!purchasedProducts.contains(productReview.getProduct())) {
            throw new IllegalStateException("Review available only after the product is delivered");
        }

        return productReview;
    }

    private ProductReviewPK buildProductReviewPK(Long productId, UUID userId) {

        Product product = productService.getProductIfExists(productId);
        User user = userService.getUserIfExists(userId);

        ProductReviewPK id = new ProductReviewPK();
        id.setProduct(product);
        id.setUser(user);

        return id;
    }

    private ProductReview buildProductReview(ProductReviewRequest productReviewRequest) {

        Product product = productService.getProductIfExists(productReviewRequest.getProductId());
        User user = userService.getUserIfExists(productReviewRequest.getUserId());

        ProductReview productReview = ProductReview.newBuilder()
                .withProduct(product)
                .withUser(user)
                .withRating(productReviewRequest.getRating())
                .withComment(productReviewRequest.getComment())
                .withMoment(Instant.now())
                .build();

        return validateProductReview(productReview);
    }
}
