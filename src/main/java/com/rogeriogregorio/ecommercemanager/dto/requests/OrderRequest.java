package com.rogeriogregorio.ecommercemanager.dto.requests;

import com.rogeriogregorio.ecommercemanager.entities.PaymentEntity;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

public class OrderRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer orderStatus;
    private Long clientId;
    private PaymentEntity paymentEntity;

    public OrderRequest() {
    }

    public OrderRequest(Long clientId) {
        this.clientId = clientId;
    }

    public OrderRequest(Long id, OrderStatus orderStatus, Long clientId) {
        this.id = id;
        setOrderStatus(orderStatus);
        this.clientId = clientId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public PaymentEntity getPaymentEntity() {
        return paymentEntity;
    }

    public void setPaymentEntity(PaymentEntity paymentEntity) {
        this.paymentEntity = paymentEntity;
    }

    @Override
    public String toString() {
        return "[Pedido: id= " + id + ", orderStatus= " + orderStatus
                + ", clientId= " + clientId + ", paymentEntity= " + paymentEntity + "]";
    }
}
