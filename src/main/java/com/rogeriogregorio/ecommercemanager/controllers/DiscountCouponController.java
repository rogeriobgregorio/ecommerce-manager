package com.rogeriogregorio.ecommercemanager.controllers;

import com.rogeriogregorio.ecommercemanager.dto.requests.DiscountCouponRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.DiscountCouponResponse;
import com.rogeriogregorio.ecommercemanager.services.DiscountCouponService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1")
public class DiscountCouponController {

    private final DiscountCouponService discountCouponService;

    @Autowired
    public DiscountCouponController(DiscountCouponService discountCouponService) {
        this.discountCouponService = discountCouponService;
    }

    @GetMapping(value = "/discount-coupons")
    public ResponseEntity<List<DiscountCouponResponse>> getAllDiscountCoupons(Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(discountCouponService.findAllDiscountCoupons(pageable).getContent());
    }

    @PostMapping(value = "/discount-coupons")
    public ResponseEntity<DiscountCouponResponse> createDiscountCoupon(@Valid @RequestBody DiscountCouponRequest discountCouponRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(discountCouponService.createDiscountCoupon(discountCouponRequest));
    }

    @GetMapping(value = "/discount-coupons/{id}")
    public ResponseEntity<DiscountCouponResponse> getDiscountCouponById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(discountCouponService.findDiscountCouponResponseById(id));
    }

    @PutMapping(value = "/discount-coupons")
    public ResponseEntity<DiscountCouponResponse> updateDiscountCoupon(@Valid @RequestBody DiscountCouponRequest discountCouponRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(discountCouponService.updateDiscountCoupon(discountCouponRequest));
    }

    @DeleteMapping(value = "/discount-coupons/{id}")
    public ResponseEntity<Void> deleteDiscountCoupon(@PathVariable Long id) {

        discountCouponService.deleteDiscountCoupon(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
