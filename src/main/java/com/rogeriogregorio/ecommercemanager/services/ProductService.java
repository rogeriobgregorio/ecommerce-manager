package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductResponse;
import com.rogeriogregorio.ecommercemanager.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public interface ProductService {

    Page<ProductResponse> findAllProducts(Pageable pageable);

    ProductResponse createProduct(ProductRequest productRequest);

    ProductResponse findProductResponseById(Long id);

    Product findProductById(Long id);

    ProductResponse updateProduct(Long id, ProductRequest productRequest);

    void deleteProduct(Long id);

    Page<ProductResponse> findProductByName(String name, Pageable pageable);
}
