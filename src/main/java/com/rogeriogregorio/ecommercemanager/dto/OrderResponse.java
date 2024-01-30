package com.rogeriogregorio.ecommercemanager.dto;

import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;

import java.time.Instant;

public class OrderResponse {

    private Long id;
    private Instant moment;
    private Integer orderStatus;
    private UserEntity client;

    public OrderResponse() { }

    public OrderResponse(Long id, Instant moment, OrderStatus orderStatus, UserEntity client) {
        this.id = id;
        this.moment = moment;
        setOrderStatus(orderStatus);
        this.client = client;
    }

    public OrderResponse(Long id, Instant moment, OrderStatus orderStatus) {
        this.id = id;
        this.moment = moment;
        setOrderStatus(orderStatus);
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

    public OrderStatus getOrderStatus() {

        return OrderStatus.valueOf(orderStatus);
    }

    public void setOrderStatus(OrderStatus orderStatus) {

        if (orderStatus == null) {
            throw new IllegalArgumentException("O status do pedido não pode ser nulo");
        }

        this.orderStatus = orderStatus.getCode();
    }

    public UserEntity getClient() {

        return client;
    }

    public void setClient(UserEntity client) {

        this.client = client;
    }
}
