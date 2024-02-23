package com.rogeriogregorio.ecommercemanager.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tb_orders")
public class OrderEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
    @Column(name = "moment")
    @NotNull(message = "O momento do pedido não pode ser nulo")
    private Instant moment;

    @Column(name = "order_status")
    @NotNull(message = "O status do pedido não pode ser nulo")
    private Integer orderStatus;

    @ManyToOne
    @JoinColumn(name = "client_id")
    @NotNull(message = "O cliente não pode ser nulo")
    private UserEntity client;


    @OneToMany(mappedBy = "id.orderEntity")
    private Set<OrderItemEntity> items = new HashSet<>();

    @OneToOne(mappedBy = "orderEntity", cascade = CascadeType.REMOVE)
    private PaymentEntity paymentEntity;

    public OrderEntity() {
    }

    public OrderEntity(Instant moment, OrderStatus orderStatus, UserEntity client) {
        this.moment = moment;
        setOrderStatus(orderStatus);
        this.client = client;
    }

    public OrderEntity(Long id, Instant moment, OrderStatus orderStatus, UserEntity client) {
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

    public Double getTotal() {
        double total = 0.0;
        for (OrderItemEntity orderItem : items) {
            total += orderItem.getSubTotal();
        }
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderEntity that = (OrderEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[id= " + id + ", moment= " + moment + ", orderStatus= " + orderStatus + ", client= " + client + "]";
    }
}
