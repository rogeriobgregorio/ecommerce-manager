package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductResponse;
import com.rogeriogregorio.ecommercemanager.entities.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProductService {

    List<ProductResponse> findAllProducts();

    ProductResponse createProduct(ProductRequest productRequest);

    ProductResponse findProductResponseById(Long id);

    Product findProductById(Long id);

    ProductResponse updateProduct(ProductRequest productRequest);

    void deleteProduct(Long id);

    List<ProductResponse> findProductByName(String name);

    Product buildProduct(ProductRequest productRequest);
}
