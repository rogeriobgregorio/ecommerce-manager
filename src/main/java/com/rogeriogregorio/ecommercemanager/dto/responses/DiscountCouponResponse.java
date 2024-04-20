package com.rogeriogregorio.ecommercemanager.dto.responses;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

public class DiscountCouponResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String code;
    private BigDecimal discount;
    private Instant validFrom;
    private Instant validUntil;

    public DiscountCouponResponse() {
    }

    public DiscountCouponResponse(Long id, String code, BigDecimal discount, Instant validFrom, Instant validUntil) {
        this.id = id;
        this.code = code;
        this.discount = discount;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isValid() {
        Instant now = Instant.now();
        return now.isAfter(validFrom) && now.isBefore(validUntil);
    }

    public Duration getRemainingValidityTime() {
        Instant now = Instant.now();
        boolean isExpired = now.isAfter(validUntil);

        return isExpired ? Duration.ZERO : Duration.between(now, validUntil);
    }
}
