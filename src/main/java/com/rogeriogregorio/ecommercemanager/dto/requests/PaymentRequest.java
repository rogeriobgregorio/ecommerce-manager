package com.rogeriogregorio.ecommercemanager.dto.requests;

import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentType;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

public class PaymentRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Instant moment;
    private Long orderId;
    private Integer paymentType;

    public PaymentRequest() {
    }

    public PaymentRequest(Long orderId, PaymentType paymentType) {

        this.orderId = orderId;
        setPaymentType(paymentType);
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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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
}
