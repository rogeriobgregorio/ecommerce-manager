package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductReviewRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public interface ProductReviewService {

    Page<ProductReviewResponse> findAllProductReviews(Pageable pageable);

    ProductReviewResponse createProductReview(ProductReviewRequest productReviewRequest);

    ProductReviewResponse findProductReviewById(Long productId, UUID userId);

    ProductReviewResponse updateProductReview(ProductReviewRequest productReviewRequest);

    void deleteProductReview(Long productId, UUID userId);
}
