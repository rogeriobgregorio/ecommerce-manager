package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductDiscountRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductDiscountResponse;
import com.rogeriogregorio.ecommercemanager.services.ProductDiscountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1")
public class ProductDiscountController {

    private final ProductDiscountService productDiscountService;

    @Autowired
    public ProductDiscountController(ProductDiscountService productDiscountService) {
        this.productDiscountService = productDiscountService;
    }

    @GetMapping(value = "/product-discounts")
    public ResponseEntity<List<ProductDiscountResponse>> getAllProductDiscounts(Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productDiscountService.findAllProductDiscounts(pageable).getContent());
    }

    @PostMapping(value = "/product-discounts")
    public ResponseEntity<ProductDiscountResponse> postProductDiscount(
            @Valid @RequestBody ProductDiscountRequest productDiscountRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productDiscountService.createProductDiscount(productDiscountRequest));
    }

    @GetMapping(value = "/product-discounts/{id}")
    public ResponseEntity<ProductDiscountResponse> getProductDiscountById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productDiscountService.findProductDiscountResponseById(id));
    }

    @PutMapping(value = "/product-discounts")
    public ResponseEntity<ProductDiscountResponse> putDiscountCoupon(
            @Valid @RequestBody ProductDiscountRequest productDiscountRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productDiscountService.updateProductDiscount(productDiscountRequest));
    }

    @DeleteMapping(value = "/product-discounts/{id}")
    public ResponseEntity<Void> deleteDiscountCoupon(@PathVariable Long id) {

        productDiscountService.deleteProductDiscount(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
