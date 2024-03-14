package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductResponse;
import com.rogeriogregorio.ecommercemanager.entities.ProductEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProductService {

    List<ProductResponse> findAllProducts();

    ProductResponse createProduct(ProductRequest productRequest);

    ProductResponse findProductById(Long id);

    ProductEntity findProductEntityById(Long id);

    ProductResponse updateProduct(ProductRequest productRequest);

    void deleteProduct(Long id);

    List<ProductResponse> findProductByName(String name);

    ProductEntity buildProductFromRequest(ProductRequest productRequest);
}
