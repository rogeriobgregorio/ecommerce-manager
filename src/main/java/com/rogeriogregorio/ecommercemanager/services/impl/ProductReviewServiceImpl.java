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
import com.rogeriogregorio.ecommercemanager.util.DataMapper;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
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
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private final Logger logger = LogManager.getLogger(ProductReviewServiceImpl.class);

    @Autowired
    public ProductReviewServiceImpl(ProductReviewRepository productReviewRepository,
                                    ProductService productService, UserService userService,
                                    ErrorHandler errorHandler, DataMapper dataMapper) {

        this.productReviewRepository = productReviewRepository;
        this.productService = productService;
        this.userService = userService;
        this.errorHandler = errorHandler;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<ProductReviewResponse> findAllProductReviews(Pageable pageable) {

        return errorHandler.catchException(() -> productReviewRepository.findAll(pageable),
                        "Error while trying to fetch all product reviews: ")
                .map(productReview -> dataMapper.toResponse(productReview, ProductReviewResponse.class));
    }

    @Transactional(readOnly = false)
    public ProductReviewResponse createProductReview(ProductReviewRequest productReviewRequest) {

        ProductReview productReview = buildProductReview(productReviewRequest);

        errorHandler.catchException(() -> productReviewRepository.save(productReview),
                "Error while trying to create product review: ");
        logger.info("Product review created: {}", productReview);

        return dataMapper.toResponse(productReview, ProductReviewResponse.class);
    }

    @Transactional(readOnly = true)
    public ProductReviewResponse findProductReviewById(Long productId, UUID userId) {

        ProductReviewPK id = buildProductReviewPK(productId, userId);

        return errorHandler.catchException(() -> productReviewRepository.findById(id),
                        "Error while trying to fetch the product review by ID: ")
                .map(productReview -> dataMapper.toResponse(productReview, ProductReviewResponse.class))
                .orElseThrow(() -> new NotFoundException("Product review not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public ProductReviewResponse updateProductReview(ProductReviewRequest productReviewRequest) {

        ProductReview productReview = buildProductReview(productReviewRequest);

        errorHandler.catchException(() -> productReviewRepository.save(productReview),
                "Error while trying to update the product review: ");
        logger.info("Product review updated: {}", productReview);

        return dataMapper.toResponse(productReview, ProductReviewResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteProductReview(Long productId, UUID userId) {

        ProductReviewPK id = buildProductReviewPK(productId, userId);

        errorHandler.catchException(() -> {
            productReviewRepository.deleteById(id);
            return null;
        }, "Error while trying to delete product review: ");
        logger.warn("Product review removed: {}", id.getProduct());

    }

    private void validateProductReview(User user, Product product) {

        Set<Product> purchasedProducts = user.getPurchasedProducts();

        if (!purchasedProducts.contains(product)) {
            throw new IllegalStateException("Review available only after the product is delivered to the client");
        }
    }

    private ProductReviewPK buildProductReviewPK(Long productId, UUID userId) {

        Product product = productService.findProductById(productId);
        User user = userService.findUserById(userId);

        ProductReviewPK id = new ProductReviewPK();
        id.setProduct(product);
        id.setUser(user);

        return id;
    }

    private ProductReview buildProductReview(ProductReviewRequest productReviewRequest) {

        Product product = productService.findProductById(productReviewRequest.getProductId());
        User user = userService.findUserById(productReviewRequest.getUserId());
        validateProductReview(user, product);
        Integer rating = productReviewRequest.getRating();
        String comment = productReviewRequest.getComment();

        return new ProductReview(product, user, rating, comment, Instant.now());
    }
}
