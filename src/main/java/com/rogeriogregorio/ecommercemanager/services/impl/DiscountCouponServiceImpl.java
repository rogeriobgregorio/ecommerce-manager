package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.DiscountCouponRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.DiscountCouponResponse;
import com.rogeriogregorio.ecommercemanager.entities.DiscountCoupon;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.DiscountCouponRepository;
import com.rogeriogregorio.ecommercemanager.services.DiscountCouponService;
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
public class DiscountCouponServiceImpl implements DiscountCouponService {

    private final DiscountCouponRepository discountCouponRepository;
    private final ErrorHandler errorHandler;
    private final DataMapper dataMapper;
    private static final Logger logger = LogManager.getLogger(DiscountCouponServiceImpl.class);

    @Autowired
    public DiscountCouponServiceImpl(DiscountCouponRepository discountCouponRepository,
                                     ErrorHandler errorHandler, DataMapper dataMapper) {

        this.discountCouponRepository = discountCouponRepository;
        this.errorHandler = errorHandler;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<DiscountCouponResponse> findAllDiscountCoupons(Pageable pageable) {

        return errorHandler.catchException(
                () -> discountCouponRepository.findAll(pageable)
                        .map(discountCoupon -> dataMapper.map(discountCoupon, DiscountCouponResponse.class)),
                "Error while trying to fetch all discount coupons: "
        );
    }

    @Transactional
    public DiscountCouponResponse createDiscountCoupon(DiscountCouponRequest discountCouponRequest) {

        validateCouponDates(discountCouponRequest);
        DiscountCoupon discountCoupon = dataMapper.map(discountCouponRequest, DiscountCoupon.class);

        DiscountCoupon savedDiscountCoupon = errorHandler.catchException(
                () -> discountCouponRepository.save(discountCoupon),
                "Error while trying to create the discount coupon: "
        );

        logger.info("Discount coupon created: {}", savedDiscountCoupon);
        return dataMapper.map(savedDiscountCoupon, DiscountCouponResponse.class);
    }

    @Transactional(readOnly = true)
    public DiscountCouponResponse findDiscountCouponById(Long id) {

        return errorHandler.catchException(
                () -> discountCouponRepository.findById(id)
                        .map(discountCoupon -> dataMapper.map(discountCoupon, DiscountCouponResponse.class))
                        .orElseThrow(() -> new NotFoundException("Discount coupon not found with ID: " + id + ".")),
                "Error while trying to find the discount coupon by ID: "
        );
    }

    @Transactional
    public DiscountCouponResponse updateDiscountCoupon(Long id, DiscountCouponRequest discountCouponRequest) {

        validateCouponDates(discountCouponRequest);
        DiscountCoupon currentDisCountCoupon = getDiscountCouponIfExists(id);
        dataMapper.map(discountCouponRequest, currentDisCountCoupon);

        DiscountCoupon updatedDiscountCoupon = errorHandler.catchException(
                () -> discountCouponRepository.save(currentDisCountCoupon),
                "Error while trying to update the discount coupon: "
        );

        logger.info("Discount Coupon updated: {}", updatedDiscountCoupon);
        return dataMapper.map(updatedDiscountCoupon, DiscountCouponResponse.class);
    }

    @Transactional
    public void deleteDiscountCoupon(Long id) {

        DiscountCoupon discountCoupon = getDiscountCouponIfExists(id);

        errorHandler.catchException(() -> {
            discountCouponRepository.delete(discountCoupon);
            return null;
        }, "Error while trying to delete the discount coupon: ");
        logger.warn("Discount coupon deleted: {}", discountCoupon);
    }

    @Transactional(readOnly = true)
    public DiscountCoupon findDiscountCouponByCode(String code) {

        return errorHandler.catchException(
                () -> discountCouponRepository.findByCode(code)
                        .orElseThrow(() -> new NotFoundException("Discount coupon not found with code: " + code + ".")),
                "Error while trying to fetch discount coupon by code: "
        );
    }

    private DiscountCoupon getDiscountCouponIfExists(Long id) {

        return errorHandler.catchException(
                () -> discountCouponRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Discount coupon not found with ID: " + id + ".")),
                "Error while trying to verify the existence of the discount coupon by ID: "
        );
    }

    private void validateCouponDates(DiscountCouponRequest discountCouponRequest) {

        Instant validFrom = discountCouponRequest.getValidFrom();
        Instant validUntil = discountCouponRequest.getValidUntil();
        boolean isValidDate = validFrom.isBefore(validUntil);

        if (!isValidDate) {
            throw new IllegalStateException("The start date must be before the end date.");
        }
    }
}
