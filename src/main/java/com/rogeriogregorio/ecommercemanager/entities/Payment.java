package com.rogeriogregorio.ecommercemanager.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentStatus;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "tb_payments")
public class Payment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
    @Column(name = "moment")
    @NotNull(message = "The payment timestamp cannot be null.")
    private Instant moment;

    @JsonIgnore
    @OneToOne
    @MapsId
    private Order order;

    @Column(name = "tx_id")
    @NotNull(message = "The txId cannot be null.")
    private String txId;

    @Column(name = "payment_method")
    @NotNull(message = "The payment type cannot be null.")
    private Integer paymentType;

    @Column(name = "charge_link")
    @NotNull(message = "The charge Link cannot be null.")
    private String chargeLink;

    @Column(name = "payment_status")
    @NotNull(message = "The payment status cannot be null.")
    private Integer paymentStatus;

    public Payment() {
    }

    private Payment(Builder builder) {
        setId(builder.id);
        setMoment(builder.moment);
        setOrder(builder.order);
        setTxId(builder.txId);
        paymentType = builder.paymentType;
        setChargeLink(builder.chargeLink);
        paymentStatus = builder.paymentStatus;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getMoment() {
        return moment;
    }

    public void setMoment(Instant moment) {
        this.moment = moment;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getAmountPaid() {

        BigDecimal amountPaid = order.getTotalFinal();

        return "Amount paid: " + amountPaid + ".";
    }

    public PaymentType getPaymentType() {
        return PaymentType.valueOf(paymentType);
    }

    public void setPaymentType(PaymentType paymentType) {

        if (paymentType == null) {
            throw new IllegalArgumentException("The payment type cannot be null.");
        }

        this.paymentType = paymentType.getCode();
    }

    public String getChargeLink() {
        return chargeLink;
    }

    public void setChargeLink(String chargeLink) {
        this.chargeLink = chargeLink;
    }

    public PaymentStatus getPaymentStatus() {
        return PaymentStatus.valueOf(paymentStatus);
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {

        if (paymentStatus == null) {
            throw new IllegalArgumentException("The payment status cannot be null.");
        }

        this.paymentStatus = paymentStatus.getCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment that = (Payment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[Payment: id= " + id
                + ", moment= " + moment
                + ", order= " + order
                + ", txId= " + txId
                + ", paymentType=" + paymentType
                + ", paymentStatus=" + paymentStatus + "]";
    }

    public Builder toBuilder() {
        return new Builder()
                .withId(this.id)
                .withMoment(this.moment)
                .withOrder(this.order)
                .withTxId(this.txId)
                .withPaymentType(PaymentType.valueOf(this.paymentType))
                .withChargeLink(this.chargeLink)
                .withPaymentStatus(PaymentStatus.valueOf(this.paymentStatus));
    }

    public static final class Builder {

        private Long id;
        private Instant moment;
        private Order order;
        private String txId;
        private Integer paymentType;
        private String chargeLink;
        private Integer paymentStatus;

        private Builder() {
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withMoment(Instant moment) {
            this.moment = moment;
            return this;
        }

        public Builder withOrder(Order order) {
            this.order = order;
            return this;
        }

        public Builder withTxId(String txId) {
            this.txId = txId;
            return this;
        }

        public Builder withPaymentType(PaymentType paymentType) {
            this.paymentType = paymentType.getCode();
            return this;
        }

        public Builder withChargeLink(String chargeLink) {
            this.chargeLink = chargeLink;
            return this;
        }

        public Builder withPaymentStatus(PaymentStatus paymentStatus) {
            this.paymentStatus = paymentStatus.getCode();
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }
}
