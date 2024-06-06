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
@RequestMapping(value = "/discount-coupons")
public class DiscountCouponController {

    private final DiscountCouponService discountCouponService;

    @Autowired
    public DiscountCouponController(DiscountCouponService discountCouponService) {
        this.discountCouponService = discountCouponService;
    }

    @GetMapping
    public ResponseEntity<List<DiscountCouponResponse>> getAllDiscountCoupons(Pageable pageable) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(discountCouponService.findAllDiscountCoupons(pageable).getContent());
    }

    @PostMapping
    public ResponseEntity<DiscountCouponResponse> postDiscountCoupon(
            @Valid @RequestBody DiscountCouponRequest discountCouponRequest) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(discountCouponService.createDiscountCoupon(discountCouponRequest));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DiscountCouponResponse> getDiscountCouponById(@PathVariable Long id) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(discountCouponService.findDiscountCouponById(id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<DiscountCouponResponse> putDiscountCoupon(@PathVariable Long id,
            @Valid @RequestBody DiscountCouponRequest discountCouponRequest) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(discountCouponService.updateDiscountCoupon(id, discountCouponRequest));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteDiscountCoupon(@PathVariable Long id) {

        discountCouponService.deleteDiscountCoupon(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
