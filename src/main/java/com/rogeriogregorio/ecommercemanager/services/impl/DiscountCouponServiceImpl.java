package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.DiscountCouponRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.DiscountCouponResponse;
import com.rogeriogregorio.ecommercemanager.entities.DiscountCoupon;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.DiscountCouponRepository;
import com.rogeriogregorio.ecommercemanager.services.DiscountCouponService;
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
public class DiscountCouponServiceImpl implements DiscountCouponService {

    private final DiscountCouponRepository discountCouponRepository;
    private final CatchError catchError;
    private final DataMapper dataMapper;
    private static final Logger LOGGER = LogManager.getLogger(DiscountCouponServiceImpl.class);

    @Autowired
    public DiscountCouponServiceImpl(DiscountCouponRepository discountCouponRepository,
                                     CatchError catchError, DataMapper dataMapper) {

        this.discountCouponRepository = discountCouponRepository;
        this.catchError = catchError;
        this.dataMapper = dataMapper;
    }

    @Transactional(readOnly = true)
    public Page<DiscountCouponResponse> findAllDiscountCoupons(Pageable pageable) {

        return catchError.run(() -> discountCouponRepository.findAll(pageable)
                .map(discountCoupon -> dataMapper.map(discountCoupon, DiscountCouponResponse.class)));
    }

    @Transactional
    public DiscountCouponResponse createDiscountCoupon(DiscountCouponRequest discountCouponRequest) {

        validateCouponDates(discountCouponRequest);
        DiscountCoupon discountCoupon = dataMapper.map(discountCouponRequest, DiscountCoupon.class);

        DiscountCoupon savedDiscountCoupon = catchError.run(() -> discountCouponRepository.save(discountCoupon));
        LOGGER.info("Discount coupon created: {}", savedDiscountCoupon);
        return dataMapper.map(savedDiscountCoupon, DiscountCouponResponse.class);
    }

    @Transactional(readOnly = true)
    public DiscountCouponResponse findDiscountCouponById(Long id) {

        return catchError.run(() -> discountCouponRepository.findById(id)
                .map(discountCoupon -> dataMapper.map(discountCoupon, DiscountCouponResponse.class))
                .orElseThrow(() -> new NotFoundException("Discount coupon not found with ID: " + id + ".")));
    }

    @Transactional
    public DiscountCouponResponse updateDiscountCoupon(Long id, DiscountCouponRequest discountCouponRequest) {

        validateCouponDates(discountCouponRequest);
        DiscountCoupon currentDisCountCoupon = getDiscountCouponIfExists(id);
        dataMapper.map(discountCouponRequest, currentDisCountCoupon);

        DiscountCoupon updatedDiscountCoupon = catchError.run(() -> discountCouponRepository.save(currentDisCountCoupon));
        LOGGER.info("Discount Coupon updated: {}", updatedDiscountCoupon);
        return dataMapper.map(updatedDiscountCoupon, DiscountCouponResponse.class);
    }

    @Transactional
    public void deleteDiscountCoupon(Long id) {

        DiscountCoupon discountCoupon = getDiscountCouponIfExists(id);

        catchError.run(() -> discountCouponRepository.delete(discountCoupon));
        LOGGER.warn("Discount coupon deleted: {}", discountCoupon);
    }

    @Transactional(readOnly = true)
    public DiscountCoupon findDiscountCouponByCode(String code) {

        return catchError.run(() -> discountCouponRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Discount coupon not found with code: " + code + ".")));
    }

    private DiscountCoupon getDiscountCouponIfExists(Long id) {

        return catchError.run(() -> discountCouponRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Discount coupon not found with ID: " + id + ".")));
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
