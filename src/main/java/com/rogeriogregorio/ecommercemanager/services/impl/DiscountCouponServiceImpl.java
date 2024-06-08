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
                .map(discountCoupon -> dataMapper.map(discountCoupon, DiscountCouponResponse.class));
    }

    @Transactional(readOnly = false)
    public DiscountCouponResponse createDiscountCoupon(DiscountCouponRequest discountCouponRequest) {

        validateCouponDates(discountCouponRequest);
        DiscountCoupon discountCoupon = dataMapper.map(discountCouponRequest, DiscountCoupon.class);

        errorHandler.catchException(() -> discountCouponRepository.save(discountCoupon),
                "Error while trying to create the discount coupon: ");
        logger.info("Discount coupon created: {}", discountCoupon);

        return dataMapper.map(discountCoupon, DiscountCouponResponse.class);
    }

    @Transactional(readOnly = true)
    public DiscountCouponResponse findDiscountCouponById(Long id) {

        return errorHandler.catchException(() -> discountCouponRepository.findById(id),
                        "Error while trying to find the discount coupon by ID: ")
                .map(discountCoupon -> dataMapper.map(discountCoupon, DiscountCouponResponse.class))
                .orElseThrow(() -> new NotFoundException("Discount coupon not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public DiscountCouponResponse updateDiscountCoupon(Long id, DiscountCouponRequest discountCouponRequest) {

        validateCouponDates(discountCouponRequest);
        DiscountCoupon currentDisCountCoupon = getDiscountCouponIfExists(id);
        DiscountCoupon updatedDiscountCoupon = dataMapper.map(discountCouponRequest, currentDisCountCoupon);

        errorHandler.catchException(() -> discountCouponRepository.save(updatedDiscountCoupon),
                "Error while trying to update the discount coupon: ");
        logger.info("Discount Coupon updated: {}", updatedDiscountCoupon);

        return dataMapper.map(updatedDiscountCoupon, DiscountCouponResponse.class);
    }

    @Transactional(readOnly = false)
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

        return errorHandler.catchException(() -> discountCouponRepository.findByCode(code),
                        "Error while trying to fetch discount coupon by code: ")
                .orElseThrow(() -> new NotFoundException("Discount coupon not found with code: " + code + "."));
    }

    private DiscountCoupon getDiscountCouponIfExists(Long id) {

        return errorHandler.catchException(() -> {

            if (!discountCouponRepository.existsById(id)) {
                throw new NotFoundException("Discount coupon not exists with ID: " + id + ".");
            }

            return dataMapper.map(discountCouponRepository.findById(id), DiscountCoupon.class);
        }, "Error while trying to verify the existence of the discount coupon by ID: ");
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
