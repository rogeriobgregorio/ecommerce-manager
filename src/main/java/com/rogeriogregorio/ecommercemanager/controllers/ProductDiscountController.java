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
@RequestMapping(value = "/product-discounts")
public class ProductDiscountController {

    private final ProductDiscountService productDiscountService;

    @Autowired
    public ProductDiscountController(ProductDiscountService productDiscountService) {
        this.productDiscountService = productDiscountService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDiscountResponse>> getAllProductDiscounts(Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productDiscountService.findAllProductDiscounts(pageable).getContent());
    }

    @PostMapping
    public ResponseEntity<ProductDiscountResponse> postProductDiscount(
            @Valid @RequestBody ProductDiscountRequest productDiscountRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productDiscountService.createProductDiscount(productDiscountRequest));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ProductDiscountResponse> getProductDiscountById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productDiscountService.findProductDiscountById(id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<ProductDiscountResponse> putDiscountCoupon(@PathVariable Long id,
            @Valid @RequestBody ProductDiscountRequest productDiscountRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productDiscountService.updateProductDiscount(id, productDiscountRequest));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteDiscountCoupon(@PathVariable Long id) {

        productDiscountService.deleteProductDiscount(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
