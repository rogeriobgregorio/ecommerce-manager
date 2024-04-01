package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductResponse;
import com.rogeriogregorio.ecommercemanager.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(value = "/products")
    public ResponseEntity<List<ProductResponse>> getAllProducts(Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.findAllProducts(pageable).getContent());
    }

    @PostMapping(value = "/products")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.createProduct(productRequest));
    }

    @GetMapping(value = "/products/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.findProductResponseById(id));
    }

    @PutMapping(value = "/products")
    public ResponseEntity<ProductResponse> updateProduct(@Valid @RequestBody ProductRequest productRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.updateProduct(productRequest));
    }

    @DeleteMapping(value = "/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping(value = "/products/search")
    public ResponseEntity<List<ProductResponse>> getProductByName(
            @RequestParam("name") String name, Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.findProductByName(name, pageable).getContent());
    }
}
