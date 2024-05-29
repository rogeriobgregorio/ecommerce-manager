package com.rogeriogregorio.ecommercemanager.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentMethod;
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
    private Integer paymentMethod;
    private String pixQRCodeLink;
    private Integer paymentStatus;


    public PaymentResponse() {
    }

    public PaymentResponse(Long id, Instant moment, Order order,
                           String txId, PaymentMethod paymentMethod,
                           String pixQRCodeLink,
                           PaymentStatus paymentStatus) {

        this.id = id;
        this.moment = moment;
        this.order = order;
        this.txId = txId;
        setPaymentMethod(paymentMethod);
        this.pixQRCodeLink = pixQRCodeLink;
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

    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.valueOf(paymentMethod);
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {

        if (paymentMethod == null) {
            throw new IllegalArgumentException("The payment method cannot be null.");
        }

        this.paymentMethod = paymentMethod.getCode();
    }

    public String getPixQRCodeLink() {
        return pixQRCodeLink;
    }

    public void setPixQRCodeLink(String pixQRCodeLink) {
        this.pixQRCodeLink = pixQRCodeLink;
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
