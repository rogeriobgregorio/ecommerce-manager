package com.rogeriogregorio.ecommercemanager.entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "tb_discount_coupons", uniqueConstraints = {
        @UniqueConstraint(columnNames = "code", name = "unique_code_constraint")
})
public class DiscountCoupon implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true)
    @NotBlank(message = "The discount coupon code cannot be blank")
    private String code;

    @Column(name = "discount")
    @NotNull(message = "The discount value cannot be null")
    @DecimalMin(value = "0.01", message = "The discount value must be greater than zero")
    private BigDecimal discount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
    @Column(name = "valid_from")
    @NotNull(message = "The coupon's validity start date cannot be null")
    private Instant validFrom;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
    @Column(name = "valid_until")
    @NotNull(message = "The coupon's expiration date cannot be null")
    private Instant validUntil;

    public DiscountCoupon() {
    }

    public DiscountCoupon(String code, BigDecimal discount, Instant validFrom, Instant validUntil) {
        this.code = code;
        this.discount = discount;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }

    public DiscountCoupon(Long id, String code, BigDecimal discount, Instant validFrom, Instant validUntil) {
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

    public String getRemainingValidityTime() {

        Instant now = Instant.now();
        boolean isExpired = now.isAfter(validUntil);

        long days = Duration.between(now, validUntil).toDays();
        long hours = Duration.between(now, validUntil).toHoursPart();
        long minutes = Duration.between(now, validUntil).toMinutesPart();

        String expiredMessage = "The coupon has expired.";
        String remainingTime = String.format("%d days, %d hours, %d minutes", days, hours, minutes);

        return isExpired ? expiredMessage : remainingTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscountCoupon that = (DiscountCoupon) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[DiscountCoupon: id= " + id
                + ", code= " + code
                + ", discount= " + discount
                + ", validFrom=" + validFrom
                + ", validUntil=" + validUntil + "]";
    }
}
