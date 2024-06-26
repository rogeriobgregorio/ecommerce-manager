package com.rogeriogregorio.ecommercemanager.dto.requests;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

public class DiscountCouponRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String code;
    private BigDecimal discount;
    private Instant validFrom;
    private Instant validUntil;

    public DiscountCouponRequest() {
    }

    public DiscountCouponRequest(String code, BigDecimal discount,
                                 Instant validFrom, Instant validUntil) {

        this.code = code;
        this.discount = discount;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public Instant getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }
}
