package com.rogeriogregorio.ecommercemanager.dto.responses;

import com.rogeriogregorio.ecommercemanager.entities.OrderItemEntity;
import com.rogeriogregorio.ecommercemanager.entities.PaymentEntity;
import com.rogeriogregorio.ecommercemanager.entities.UserEntity;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class OrderResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Instant moment;
    private Integer orderStatus;
    private UserEntity client;
    private Set<OrderItemEntity> items = new HashSet<>();
    private PaymentEntity paymentEntity;

    public OrderResponse() {
    }

    public OrderResponse(Long id, Instant moment, OrderStatus orderStatus, UserEntity client) {
        this.id = id;
        this.moment = moment;
        setOrderStatus(orderStatus);
        this.client = client;
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

    public PaymentEntity getPaymentEntity() {
        return paymentEntity;
    }

    public void setPaymentEntity(PaymentEntity paymentEntity) {
        this.paymentEntity = paymentEntity;
    }

    public Set<OrderItemEntity> getItems() {
        return items;
    }

    public void setItems(Set<OrderItemEntity> items) {
        this.items = items;
    }

    public Double getTotal() {
        double total = 0.0;
        for (OrderItemEntity orderItem : items) {
            total += orderItem.getSubTotal();
        }
        return total;
    }
}
