package com.rogeriogregorio.ecommercemanager.dto.requests;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

public class PaymentRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Instant moment;
    private Long orderId;

    public PaymentRequest() {
    }

    public PaymentRequest(Instant moment, Long orderId) {
        this.moment = moment;
        this.orderId = orderId;
    }

    public PaymentRequest(Long id, Instant moment, Long orderId) {
        this.id = id;
        this.moment = moment;
        this.orderId = orderId;
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
}
