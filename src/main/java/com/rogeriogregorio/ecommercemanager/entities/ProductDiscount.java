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
@Table(name = "tb_product_discounts")
public class ProductDiscount implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    @NotBlank(message = "The name must not be blank.")
    @Size(max = 250, message = "The name must have a maximum of 250 characters.")
    private String name;

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
    @NotNull(message = "The discount's expiration date cannot be null")
    private Instant validUntil;

    public ProductDiscount() {
    }

    public ProductDiscount(String name, BigDecimal discount,
                           Instant validFrom, Instant validUntil) {

        this.name = name;
        this.discount = discount;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }

    public ProductDiscount(Long id, String name, BigDecimal discount,
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDiscount that = (ProductDiscount) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[ProductDiscount: id= " + id
                + ", name= " + name
                + ", discount= " + discount
                + ", validFrom=" + validFrom
                + ", validUntil=" + validUntil + "]";
    }
}
