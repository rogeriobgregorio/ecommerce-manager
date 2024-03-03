package com.rogeriogregorio.ecommercemanager.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "tb_payments")
public class PaymentEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
    @Column(name = "moment")
    @NotNull(message = "O momento do pagamento não pode ser nulo")
    private Instant moment;

    @JsonIgnore
    @OneToOne
    @MapsId
    private OrderEntity orderEntity;

    public PaymentEntity() {
    }

    public PaymentEntity(Instant moment, OrderEntity orderEntity) {
        this.moment = moment;
        this.orderEntity = orderEntity;
    }

    public PaymentEntity(Long id, Instant moment, OrderEntity orderEntity) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentEntity that = (PaymentEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[Pagamento: id= " + id + ", moment= " + moment + ", orderEntity= " + orderEntity +"]";
    }
}
