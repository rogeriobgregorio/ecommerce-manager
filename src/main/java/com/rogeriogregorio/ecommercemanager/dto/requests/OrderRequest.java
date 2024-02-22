package com.rogeriogregorio.ecommercemanager.dto.requests;

import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

public class OrderRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Instant moment;
    private Integer orderStatus;
    private Long clientId;

    public OrderRequest() {
    }

    public OrderRequest(Long id, Instant moment, OrderStatus orderStatus, Long clientId) {
        this.id = id;
        this.moment = moment;
        setOrderStatus(orderStatus);
        this.clientId = clientId;
    }

    public OrderRequest(Instant moment, OrderStatus orderStatus, Long clientId) {
        this.moment = moment;
        setOrderStatus(orderStatus);
        this.clientId = clientId;
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

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
