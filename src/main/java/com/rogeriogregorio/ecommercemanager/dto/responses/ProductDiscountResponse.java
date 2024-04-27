package com.rogeriogregorio.ecommercemanager.dto.responses;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

public class ProductDiscountResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private BigDecimal discount;
    private Instant validFrom;
    private Instant validUntil;

    public ProductDiscountResponse() {
    }

    public ProductDiscountResponse(String name, BigDecimal discount,
                                   Instant validFrom, Instant validUntil) {

        this.name = name;
        this.discount = discount;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }

    public ProductDiscountResponse(Long id, String name, BigDecimal discount,
                                   Instant validFrom, Instant validUntil) {

        this.id = id;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getRemainingValidityTime() {

        Instant now = Instant.now();
        boolean isExpired = now.isAfter(validUntil);

        long days = Duration.between(now, validUntil).toDays();
        long hours = Duration.between(now, validUntil).toHoursPart();
        long minutes = Duration.between(now, validUntil).toMinutesPart();

        String expiredMessage = "The discount has expired.";
        String remainingTime = String.format("%d days, %d hours, %d minutes", days, hours, minutes);

        return isExpired ? expiredMessage : remainingTime;
    }
}
