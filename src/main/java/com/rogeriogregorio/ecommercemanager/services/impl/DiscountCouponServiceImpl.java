package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.requests.DiscountCouponRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.DiscountCouponResponse;
import com.rogeriogregorio.ecommercemanager.entities.DiscountCoupon;
import com.rogeriogregorio.ecommercemanager.exceptions.NotFoundException;
import com.rogeriogregorio.ecommercemanager.repositories.DiscountCouponRepository;
import com.rogeriogregorio.ecommercemanager.services.DiscountCouponService;
import com.rogeriogregorio.ecommercemanager.services.template.ErrorHandlerTemplateImpl;
import com.rogeriogregorio.ecommercemanager.util.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class DiscountCouponServiceImpl extends ErrorHandlerTemplateImpl implements DiscountCouponService {

    private final DiscountCouponRepository discountCouponRepository;
    private final Converter converter;

    @Autowired
    public DiscountCouponServiceImpl(DiscountCouponRepository discountCouponRepository, Converter converter) {
        this.discountCouponRepository = discountCouponRepository;
        this.converter = converter;
    }

    @Transactional(readOnly = true)
    public Page<DiscountCouponResponse> findAllDiscountCoupons(Pageable pageable) {

        return handleError(() -> discountCouponRepository.findAll(pageable),
                "Error while trying to fetch all discount coupons: ")
                .map(discountCoupon -> converter.toResponse(discountCoupon, DiscountCouponResponse.class));
    }

    @Transactional(readOnly = false)
    public DiscountCouponResponse createDiscountCoupon(DiscountCouponRequest discountCouponRequest) {

        discountCouponRequest.setId(null);
        DiscountCoupon discountCoupon = buildDiscountCoupon(discountCouponRequest);

        handleError(() -> discountCouponRepository.save(discountCoupon),
                "Error while trying to create the discount coupon: ");
        logger.info("Discount coupon created: {}", discountCoupon);

        return converter.toResponse(discountCoupon, DiscountCouponResponse.class);
    }

    @Transactional(readOnly = true)
    public DiscountCouponResponse findDiscountCouponResponseById(Long id) {

        return handleError(() -> discountCouponRepository.findById(id),
                "Error while trying to find the discount coupon by ID: ")
                .map(discountCoupon -> converter.toResponse(discountCoupon, DiscountCouponResponse.class))
                .orElseThrow(() -> new NotFoundException("Discount coupon not found with ID: " + id + "."));
    }

    @Transactional(readOnly = false)
    public DiscountCouponResponse updateDiscountCoupon(DiscountCouponRequest discountCouponRequest) {

        findDiscountCouponById(discountCouponRequest.getId());
        DiscountCoupon discountCoupon = buildDiscountCoupon(discountCouponRequest);

        handleError(() -> discountCouponRepository.save(discountCoupon),
                "Error while trying to update the discount coupon: ");
        logger.info("Discount Coupon updated: {}", discountCoupon);

        return converter.toResponse(discountCoupon, DiscountCouponResponse.class);
    }

    @Transactional(readOnly = false)
    public void deleteDiscountCoupon(Long id) {

        DiscountCoupon discountCoupon = findDiscountCouponById(id);

        handleError(() -> {
            discountCouponRepository.deleteById(id);
            return null;
        }, "Error while trying to delete the discount coupon: ");
        logger.warn("Discount Coupon removed: {}", discountCoupon);
    }

    @Transactional(readOnly = true)
    public DiscountCoupon findDiscountCouponByCode(String code) {

        return handleError(() -> discountCouponRepository.findByCode(code),
                "Error while trying to fetch discount coupon by code: ")
                .orElseThrow(() -> new NotFoundException("Discount coupon not found with code: " + code + "."));
    }

    public DiscountCoupon findDiscountCouponById(Long id) {

        return handleError(() -> discountCouponRepository.findById(id),
                "Error while trying to find the discount coupon by ID: ")
                .orElseThrow(() -> new NotFoundException("Discount coupon not found with ID: " + id + "."));
    }

    private void validateCouponDates(DiscountCouponRequest discountCouponRequest) {
        Instant validFrom = discountCouponRequest.getValidFrom();
        Instant validUntil = discountCouponRequest.getValidUntil();
        boolean isValidDate = validFrom.isBefore(validUntil);

        if (!isValidDate) {
            throw new IllegalStateException("The start date must be before the end date.");
        }
    }

    public DiscountCoupon buildDiscountCoupon(DiscountCouponRequest discountCouponRequest) {

        validateCouponDates(discountCouponRequest);

        return converter.toEntity(discountCouponRequest, DiscountCoupon.class);
    }
}
