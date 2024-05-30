package com.rogeriogregorio.ecommercemanager.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentType;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentStatus;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

public class PaymentResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Instant moment;
    private Order order;
    private String txId;
    private Integer paymentType;
    private String chargeLink;
    private Integer paymentStatus;


    public PaymentResponse() {
    }

    public PaymentResponse(Long id, Instant moment, Order order,
                           String txId, PaymentType paymentType,
                           String chargeLink, PaymentStatus paymentStatus) {

        this.id = id;
        this.moment = moment;
        this.order = order;
        this.txId = txId;
        setPaymentType(paymentType);
        this.chargeLink = chargeLink;
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

    @JsonIgnore
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

    public String getAmountPaid() {

        BigDecimal amountPaid = order.getTotalFinal();

        return "Amount paid: " + amountPaid + ".";
    }
}
