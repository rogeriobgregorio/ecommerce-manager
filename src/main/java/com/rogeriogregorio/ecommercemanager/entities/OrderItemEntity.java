package com.rogeriogregorio.ecommercemanager.entities;

import com.rogeriogregorio.ecommercemanager.entities.primarykey.OrderItemPK;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tb_order_item")
public class OrderItemEntity implements Serializable {

    @EmbeddedId
    private OrderItemPK id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    private Double price;

    public OrderItemEntity() {
    }

    public OrderItemEntity(OrderEntity orderEntity, ProductEntity productEntity, Integer quantity, Double price) {

        id.setOrderEntity(orderEntity);
        id.setProductEntity(productEntity);
        this.quantity = quantity;
        this.price = price;
    }

    public OrderEntity getOrderEntity() {
        return id.getOrderEntity();
    }

    public void setOrderEntity(OrderEntity orderEntity) {
        id.setOrderEntity(orderEntity);
    }

    public ProductEntity getProductEntity() {
        return id.getProductEntity();
    }

    public void setProductEntity(ProductEntity productEntity) {
        id.setProductEntity(productEntity);
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemEntity that = (OrderItemEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
