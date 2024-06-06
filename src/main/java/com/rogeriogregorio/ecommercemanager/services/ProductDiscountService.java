package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductDiscountRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductDiscountResponse;
import com.rogeriogregorio.ecommercemanager.entities.ProductDiscount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductDiscountService {

    Page<ProductDiscountResponse> findAllProductDiscounts(Pageable pageable);

    ProductDiscountResponse createProductDiscount(ProductDiscountRequest productDiscountRequest);

    ProductDiscountResponse findProductDiscountResponseById(Long id);

    ProductDiscount findProductDiscountById(Long id);

    ProductDiscountResponse updateProductDiscount(Long id, ProductDiscountRequest productDiscountRequest);

    void deleteProductDiscount(Long id);
}
