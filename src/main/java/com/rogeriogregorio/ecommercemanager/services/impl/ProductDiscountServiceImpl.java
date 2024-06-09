package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductDiscountRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductDiscountResponse;
import com.rogeriogregorio.ecommercemanager.entities.ProductDiscount;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.ProductDiscountRepository;
import com.rogeriogregorio.ecommercemanager.services.ProductDiscountService;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import com.rogeriogregorio.ecommercemanager.utils.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ProductDiscountServiceImpl implements ProductDiscountService {

    private final ProductDiscountRepository productDiscountRepository;
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private final Logger logger = LogManager.getLogger(ProductDiscountServiceImpl.class);

    @Autowired
    public ProductDiscountServiceImpl(ProductDiscountRepository productDiscountRepository,
                                      ErrorHandler errorHandler, DataMapper dataMapper) {

        this.productDiscountRepository = productDiscountRepository;
        this.errorHandler = errorHandler;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<ProductDiscountResponse> findAllProductDiscounts(Pageable pageable) {

        return errorHandler.catchException(() -> productDiscountRepository.findAll(pageable),
                        "Error while trying to fetch all products discounts: ")
                .map(productDiscount -> dataMapper.map(productDiscount, ProductDiscountResponse.class));
    }

    @Transactional(readOnly = false)
    public ProductDiscountResponse createProductDiscount(ProductDiscountRequest productDiscountRequest) {

        validateProductDiscountDates(productDiscountRequest);
        ProductDiscount productDiscount = dataMapper.map(productDiscountRequest, ProductDiscount.class);

        errorHandler.catchException(() -> productDiscountRepository.save(productDiscount),
                "Error while trying to create the product discount: ");
        logger.info("Product discount created: {}", productDiscount);

        return dataMapper.map(productDiscount, ProductDiscountResponse.class);
    }

    @Transactional(readOnly = true)
    public ProductDiscountResponse findProductDiscountById(Long id) {

        return errorHandler.catchException(() -> productDiscountRepository.findById(id),
                        "Error while trying to find the product discount by ID: ")
                .map(productDiscount -> dataMapper.map(productDiscount, ProductDiscountResponse.class))
                .orElseThrow(() -> new NotFoundException("Product discount response not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public ProductDiscountResponse updateProductDiscount(Long id, ProductDiscountRequest productDiscountRequest) {

        validateProductDiscountDates(productDiscountRequest);
        ProductDiscount currentProductDiscount = getProductDiscountIfExists(id);
        ProductDiscount updatedProductDiscount = dataMapper.map(productDiscountRequest, currentProductDiscount);

        errorHandler.catchException(() -> productDiscountRepository.save(updatedProductDiscount),
                "Error while trying to update the product discount: ");
        logger.info("Product discount updated: {}", updatedProductDiscount);

        return dataMapper.map(updatedProductDiscount, ProductDiscountResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteProductDiscount(Long id) {

        ProductDiscount productDiscount = getProductDiscountIfExists(id);

        errorHandler.catchException(() -> {
            productDiscountRepository.delete(productDiscount);
            return null;
        }, "Error while trying to delete the product discount: ");
        logger.warn("Product discount deleted: {}", productDiscount);
    }

    public ProductDiscount getProductDiscountIfExists(Long id) {

        return errorHandler.catchException(() -> {

            if (!productDiscountRepository.existsById(id)) {
                throw new NotFoundException("Product discount not exists with ID: " + id + ".");
            }

            return dataMapper.map(productDiscountRepository.findById(id), ProductDiscount.class);
        }, "Error while trying to verify the existence of the product discount by ID: ");
    }

    private void validateProductDiscountDates(ProductDiscountRequest productDiscountRequest) {

        Instant validFrom = productDiscountRequest.getValidFrom();
        Instant validUntil = productDiscountRequest.getValidUntil();
        boolean isValidDate = validFrom.isBefore(validUntil);

        if (!isValidDate) {
            throw new IllegalStateException("The start date must be before the end date.");
        }
    }
}
