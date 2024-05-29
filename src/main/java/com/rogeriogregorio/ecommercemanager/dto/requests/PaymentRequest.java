package com.rogeriogregorio.ecommercemanager.dto.requests;

import com.rogeriogregorio.ecommercemanager.entities.enums.PaymentMethod;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

public class PaymentRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Instant moment;
    private Long orderId;
    private Integer paymentMethod;

    public PaymentRequest() {
    }

    public PaymentRequest(Long orderId, PaymentMethod paymentMethod) {

        this.orderId = orderId;
        setPaymentMethod(paymentMethod);
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

    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.valueOf(paymentMethod);
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {

        if (paymentMethod == null) {
            throw new IllegalArgumentException("The payment method cannot be null.");
        }

        this.paymentMethod = paymentMethod.getCode();
    }
}
