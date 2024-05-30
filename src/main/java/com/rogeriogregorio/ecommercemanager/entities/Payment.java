package com.rogeriogregorio.ecommercemanager.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentType;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentStatus;
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
    private String txId;

    @Column(name = "payment_method")
    @NotNull(message = "The payment type cannot be null.")
    private Integer paymentType;

    @Column(name = "charge_link")
    private String chargeLink;

    @Column(name = "payment_status")
    @NotNull(message = "The payment status cannot be null.")
    private Integer paymentStatus;

    public Payment() {
    }

    public Payment(Instant moment, Order order,
                   PaymentType paymentType,
                   PaymentStatus paymentStatus) {

        this.moment = moment;
        this.order = order;
        setPaymentType(paymentType);
        setPaymentStatus(paymentStatus);
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
}
