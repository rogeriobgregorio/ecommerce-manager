package com.rogeriogregorio.ecommercemanager.dto.requests;

import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

public class PaymentRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Instant moment;
    private OrderEntity orderEntity;

    public PaymentRequest() {
    }

    public PaymentRequest(Instant moment, OrderEntity orderEntity) {
        this.moment = moment;
        this.orderEntity = orderEntity;
    }

    public PaymentRequest(Long id, Instant moment, OrderEntity orderEntity) {
        this.id = id;
        this.moment = moment;
        this.orderEntity = orderEntity;
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

    public OrderEntity getOrderEntity() {
        return orderEntity;
    }

    public void setOrderEntity(OrderEntity orderEntity) {
        this.orderEntity = orderEntity;
    }
}
