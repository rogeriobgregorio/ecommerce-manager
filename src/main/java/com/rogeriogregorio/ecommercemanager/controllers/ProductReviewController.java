package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductReviewRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductReviewResponse;
import com.rogeriogregorio.ecommercemanager.services.ProductReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1")
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    @Autowired
    public ProductReviewController(ProductReviewService productReviewService) {
        this.productReviewService = productReviewService;
    }

    @GetMapping(value = "/product-reviews")
    public ResponseEntity<List<ProductReviewResponse>> getAllProductReviews(Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productReviewService.findAllProductReviews(pageable).getContent());
    }

    @PostMapping(value = "/product-reviews")
    public ResponseEntity<ProductReviewResponse> postProductReview(
            @Valid @RequestBody ProductReviewRequest productReviewRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productReviewService.createProductReview(productReviewRequest));
    }

    @GetMapping(value = "/product-reviews/{productId}/{userId}")
    public ResponseEntity<ProductReviewResponse> getProductReviewById(
            @PathVariable Long productId, @PathVariable Long userId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productReviewService.findProductReviewById(productId, userId));
    }

    @PutMapping(value = "/product-reviews")
    public ResponseEntity<ProductReviewResponse> putProductReview(
            @Valid @RequestBody ProductReviewRequest productReviewRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productReviewService.updateProductReview(productReviewRequest));
    }

    @DeleteMapping(value = "/product-reviews/{productId}/{userId}")
    public ResponseEntity<Void> deleteProductReview(
            @PathVariable Long productId, @PathVariable Long userId) {

        productReviewService.deleteProductReview(productId, userId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
