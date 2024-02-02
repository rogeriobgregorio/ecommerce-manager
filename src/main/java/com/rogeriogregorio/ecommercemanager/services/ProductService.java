package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.ProductRequest;
import com.rogeriogregorio.ecommercemanager.dto.ProductResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProductService {

    public List<ProductResponse> findAllProducts();

    public ProductResponse createProduct(ProductRequest productRequest);

    public ProductResponse findProductById(Long id);

    public ProductResponse updateProduct(ProductRequest productRequest);

    public void deleteProduct(Long id);

    public List<ProductResponse> findProductByName(String name);
}
