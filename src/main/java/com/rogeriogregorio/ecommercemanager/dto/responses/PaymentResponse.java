package com.rogeriogregorio.ecommercemanager.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rogeriogregorio.ecommercemanager.entities.Order;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

public class PaymentResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Instant moment;
    @JsonIgnore
    private Order order;

    public PaymentResponse() {
    }

    public PaymentResponse(Long id, Instant moment, Order order) {
        this.id = id;
        this.moment = moment;
        this.order = order;
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

    public Order getOrderEntity() {
        return order;
    }

    public void setOrderEntity(Order order) {
        this.order = order;
    }
}
