package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.ProductDiscountRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.ProductDiscountResponse;
import com.rogeriogregorio.ecommercemanager.entities.ProductDiscount;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.ProductDiscountRepository;
import com.rogeriogregorio.ecommercemanager.services.ProductDiscountService;
import com.rogeriogregorio.ecommercemanager.utils.DataMapper;
import com.rogeriogregorio.ecommercemanager.utils.CatchError;
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
    private final CatchError catchError;
    private final DataMapper dataMapper;
    private static final Logger LOGGER = LogManager.getLogger(ProductDiscountServiceImpl.class);

    @Autowired
    public ProductDiscountServiceImpl(ProductDiscountRepository productDiscountRepository,
                                      CatchError catchError, DataMapper dataMapper) {

        this.productDiscountRepository = productDiscountRepository;
        this.catchError = catchError;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<ProductDiscountResponse> findAllProductDiscounts(Pageable pageable) {

        return catchError.run(() -> productDiscountRepository.findAll(pageable)
                .map(productDiscount -> dataMapper.map(productDiscount, ProductDiscountResponse.class)));
    }

    @Transactional
    public ProductDiscountResponse createProductDiscount(ProductDiscountRequest productDiscountRequest) {

        validateProductDiscountDates(productDiscountRequest);
        ProductDiscount productDiscount = dataMapper.map(productDiscountRequest, ProductDiscount.class);

        ProductDiscount savedProductDiscount = catchError.run(() -> productDiscountRepository.save(productDiscount));
        LOGGER.info("Product discount created: {}", savedProductDiscount);
        return dataMapper.map(savedProductDiscount, ProductDiscountResponse.class);
    }

    @Transactional(readOnly = true)
    public ProductDiscountResponse findProductDiscountById(Long id) {

        return catchError.run(() -> productDiscountRepository.findById(id)
                .map(productDiscount -> dataMapper.map(productDiscount, ProductDiscountResponse.class))
                .orElseThrow(() -> new NotFoundException("Product discount not found with ID: " + id + ".")));
    }

    @Transactional
    public ProductDiscountResponse updateProductDiscount(Long id, ProductDiscountRequest productDiscountRequest) {

        validateProductDiscountDates(productDiscountRequest);
        ProductDiscount currentProductDiscount = getProductDiscountIfExists(id);
        dataMapper.map(productDiscountRequest, currentProductDiscount);

        ProductDiscount updatedProductDiscount = catchError.run(() -> productDiscountRepository.save(currentProductDiscount));
        LOGGER.info("Product discount updated: {}", updatedProductDiscount);
        return dataMapper.map(updatedProductDiscount, ProductDiscountResponse.class);
    }

    @Transactional
    public void deleteProductDiscount(Long id) {

        ProductDiscount productDiscount = getProductDiscountIfExists(id);

        catchError.run(() -> productDiscountRepository.delete(productDiscount));
        LOGGER.warn("Product discount deleted: {}", productDiscount);
    }

    public ProductDiscount getProductDiscountIfExists(Long id) {

        return catchError.run(() -> productDiscountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product discount response not found with ID: " + id + ".")));
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
