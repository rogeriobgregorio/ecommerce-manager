package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.DiscountCouponRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.DiscountCouponResponse;
import com.rogeriogregorio.ecommercemanager.entities.DiscountCoupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public interface DiscountCouponService {

    Page<DiscountCouponResponse> findAllDiscountCoupons(Pageable pageable);

    DiscountCouponResponse createDiscountCoupon(DiscountCouponRequest discountCouponRequest);

    DiscountCouponResponse findDiscountCouponById(Long id);

    DiscountCouponResponse updateDiscountCoupon(Long id, DiscountCouponRequest discountCouponRequest);

    void deleteDiscountCoupon(Long id);

    DiscountCoupon findDiscountCouponByCode(String code);
}
