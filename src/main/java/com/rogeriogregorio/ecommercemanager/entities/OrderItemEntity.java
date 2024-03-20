package com.rogeriogregorio.ecommercemanager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rogeriogregorio.ecommercemanager.entities.primarykey.OrderItemPK;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "tb_order_item")
public class OrderItemEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private OrderItemPK id = new OrderItemPK();

    @NotNull(message = "A quantidade não pode ser nula")
    @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    @NotNull(message = "O preço não pode ser nulo")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que 0")
    private BigDecimal price;

    public OrderItemEntity() {
    }

    public OrderItemEntity(OrderEntity orderEntity, ProductEntity productEntity, Integer quantity, Double price) {

        id.setOrderEntity(orderEntity);
        id.setProductEntity(productEntity);
        this.quantity = quantity;
        this.price = BigDecimal.valueOf(price);
    }

    public OrderItemEntity(OrderEntity orderEntity, ProductEntity productEntity, Integer quantity, BigDecimal price) {

        id.setOrderEntity(orderEntity);
        id.setProductEntity(productEntity);
        this.quantity = quantity;
        this.price = price;
    }

    @JsonIgnore
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = BigDecimal.valueOf(price);
    }

    public BigDecimal getSubTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
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

    @Override
    public String toString() {
        return "[Item do pedido: id= " + id + ", quantity= " + quantity +", price= " + price +"]";
    }
}
