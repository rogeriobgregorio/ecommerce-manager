package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductDiscountRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductDiscountResponse;
import com.rogeriogregorio.ecommercemanager.entities.ProductDiscount;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.ProductDiscountRepository;
import com.rogeriogregorio.ecommercemanager.services.ProductDiscountService;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplate;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ProductDiscountServiceImpl implements ProductDiscountService {

    private final ProductDiscountRepository productDiscountRepository;
    private final ErrorHandlerTemplate errorHandler;
    private final Converter converter;
    private final Logger logger = LogManager.getLogger();

    @Autowired
    public ProductDiscountServiceImpl(ProductDiscountRepository productDiscountRepository,
                                      ErrorHandlerTemplate errorHandler, Converter converter) {

        this.productDiscountRepository = productDiscountRepository;
        this.errorHandler = errorHandler;
        this.converter = converter;
    }

    public Page<ProductDiscountResponse> findAllProductDiscounts(Pageable pageable) {

        return errorHandler.catchException(() -> productDiscountRepository.findAll(pageable),
                        "Error while trying to fetch all products discounts: ")
                .map(productDiscount -> converter.toResponse(productDiscount, ProductDiscountResponse.class));
    }

    public ProductDiscountResponse createProductDiscount(ProductDiscountRequest productDiscountRequest) {

        productDiscountRequest.setId(null);
        ProductDiscount productDiscount = buildProductDiscount(productDiscountRequest);

        errorHandler.catchException(() -> productDiscountRepository.save(productDiscount),
                "Error while trying to create the product discount: ");
        logger.info("Product discount created: {}", productDiscount);

        return converter.toResponse(productDiscount, ProductDiscountResponse.class);
    }

    public ProductDiscountResponse findProductDiscountResponseById(Long id) {

        return errorHandler.catchException(() -> productDiscountRepository.findById(id),
                        "Error while trying to find the product discount by ID: ")
                .map(productDiscount -> converter.toResponse(productDiscount, ProductDiscountResponse.class))
                .orElseThrow(() -> new NotFoundException("Product discount response not found with ID: " + id + "."));
    }

    public ProductDiscountResponse updateProductDiscount(ProductDiscountRequest productDiscountRequest) {

        isProductDiscountExists(productDiscountRequest.getId());
        ProductDiscount productDiscount = buildProductDiscount(productDiscountRequest);

        errorHandler.catchException(() -> productDiscountRepository.save(productDiscount),
                "Error while trying to update the product discount: ");
        logger.info("Product discount updated: {}", productDiscount);

        return converter.toResponse(productDiscount, ProductDiscountResponse.class);
    }

    public void deleteProductDiscount(Long id) {

        isProductDiscountExists(id);

        errorHandler.catchException(() -> {
            productDiscountRepository.deleteById(id);
            return null;
        }, "Error while trying to delete the product discount: ");
        logger.warn("Product discount removed: {}", id);
    }

    public ProductDiscount findProductDiscountById(Long id) {

        return errorHandler.catchException(() -> productDiscountRepository.findById(id),
                        "Error while trying to find the product discount by ID: ")
                .orElseThrow(() -> new NotFoundException("Product discount not found with ID: " + id + "."));
    }

    private void isProductDiscountExists(Long id) {

        boolean isProductDiscountExists = errorHandler.catchException(() -> productDiscountRepository.existsById(id),
                "Error while trying to check the presence of the product discount: ");

        if (!isProductDiscountExists) {
            throw new NotFoundException("Product discount not found with ID: " + id + ".");
        }
    }

    private void validateProductDiscountDates(ProductDiscountRequest productDiscountRequest) {

        Instant validFrom = productDiscountRequest.getValidFrom();
        Instant validUntil = productDiscountRequest.getValidUntil();
        boolean isValidDate = validFrom.isBefore(validUntil);

        if (!isValidDate) {
            throw new IllegalStateException("The start date must be before the end date.");
        }
    }

    private ProductDiscount buildProductDiscount(ProductDiscountRequest productDiscountRequest) {

        validateProductDiscountDates(productDiscountRequest);

        return converter.toEntity(productDiscountRequest, ProductDiscount.class);
    }
}