package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.DiscountCouponRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.DiscountCouponResponse;
import com.rogeriogregorio.ecommercemanager.entities.DiscountCoupon;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.DiscountCouponRepository;
import com.rogeriogregorio.ecommercemanager.services.DiscountCouponService;
import com.rogeriogregorio.ecommercemanager.util.DataMapper;
import com.rogeriogregorio.ecommercemanager.util.ErrorHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class DiscountCouponServiceImpl implements DiscountCouponService {

    private final DiscountCouponRepository discountCouponRepository;
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private final Logger logger = LogManager.getLogger(DiscountCouponServiceImpl.class);

    @Autowired
    public DiscountCouponServiceImpl(DiscountCouponRepository discountCouponRepository,
                                     ErrorHandler errorHandler, DataMapper dataMapper) {

        this.discountCouponRepository = discountCouponRepository;
        this.errorHandler = errorHandler;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<DiscountCouponResponse> findAllDiscountCoupons(Pageable pageable) {

        return errorHandler.catchException(() -> discountCouponRepository.findAll(pageable),
                        "Error while trying to fetch all discount coupons: ")
                .map(discountCoupon -> dataMapper.toResponse(discountCoupon, DiscountCouponResponse.class));
    }

    @Transactional(readOnly = false)
    public DiscountCouponResponse createDiscountCoupon(DiscountCouponRequest discountCouponRequest) {

        DiscountCoupon discountCoupon = validateCouponDates(discountCouponRequest);

        errorHandler.catchException(() -> discountCouponRepository.save(discountCoupon),
                "Error while trying to create the discount coupon: ");
        logger.info("Discount coupon created: {}", discountCoupon);

        return dataMapper.toResponse(discountCoupon, DiscountCouponResponse.class);
    }

    @Transactional(readOnly = true)
    public DiscountCouponResponse findDiscountCouponById(Long id) {

        return errorHandler.catchException(() -> discountCouponRepository.findById(id),
                        "Error while trying to find the discount coupon by ID: ")
                .map(discountCoupon -> dataMapper.toResponse(discountCoupon, DiscountCouponResponse.class))
                .orElseThrow(() -> new NotFoundException("Discount coupon not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public DiscountCouponResponse updateDiscountCoupon(Long id, DiscountCouponRequest discountCouponRequest) {

        verifyDiscountCouponExists(id);
        DiscountCoupon discountCoupon = validateCouponDates(discountCouponRequest);

        errorHandler.catchException(() -> discountCouponRepository.save(discountCoupon),
                "Error while trying to update the discount coupon: ");
        logger.info("Discount Coupon updated: {}", discountCoupon);

        return dataMapper.toResponse(discountCoupon, DiscountCouponResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteDiscountCoupon(Long id) {

        verifyDiscountCouponExists(id);

        errorHandler.catchException(() -> {
            discountCouponRepository.deleteById(id);
            return null;
        }, "Error while trying to delete the discount coupon: ");
        logger.warn("Discount Coupon removed: {}", id);
    }

    @Transactional(readOnly = true)
    public DiscountCoupon findDiscountCouponByCode(String code) {

        return errorHandler.catchException(() -> discountCouponRepository.findByCode(code),
                        "Error while trying to fetch discount coupon by code: ")
                .orElseThrow(() -> new NotFoundException("Discount coupon not found with code: " + code + "."));
    }

    private void verifyDiscountCouponExists(Long id) {

        boolean isExistsDiscountCoupon = errorHandler.catchException(() -> discountCouponRepository.existsById(id),
                "Error while trying to check the presence of the discount coupon: ");

        if (!isExistsDiscountCoupon) {
            throw new NotFoundException("Discount coupon not found with ID: " + id + ".");
        }
    }

    private DiscountCoupon validateCouponDates(DiscountCouponRequest discountCouponRequest) {

        Instant validFrom = discountCouponRequest.getValidFrom();
        Instant validUntil = discountCouponRequest.getValidUntil();
        boolean isValidDate = validFrom.isBefore(validUntil);

        if (!isValidDate) {
            throw new IllegalStateException("The start date must be before the end date.");
        }

        return dataMapper.toEntity(discountCouponRequest, DiscountCoupon.class);
    }
}
