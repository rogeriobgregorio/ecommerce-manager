package com.rogeriogregorio.ecommercemanager.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rogeriogregorio.ecommercemanager.entities.primarykeys.OrderItemPK;
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
@Table(name = "tb_order_items")
public class OrderItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private OrderItemPK id = new OrderItemPK();

    @NotNull(message = "The quantity cannot be null.")
    @Min(value = 1, message = "The quantity must be at least 1.")
    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    @NotNull(message = "The price cannot be null.")
    @DecimalMin(value = "0.01", message = "The price must be greater than 0.")
    private BigDecimal price;

    public OrderItem() {
    }

    private OrderItem(Builder builder) {
        id.setOrder(builder.order);
        id.setProduct(builder.product);
        setQuantity(builder.quantity);
        setPrice(builder.price);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @JsonIgnore
    public Order getOrder() {
        return id.getOrder();
    }

    public void setOrder(Order order) {
        id.setOrder(order);
    }

    public Product getProduct() {
        return id.getProduct();
    }

    public void setProduct(Product product) {
        id.setProduct(product);
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

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSubTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem that = (OrderItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "[OrderItem: id= " + id
                + ", quantity= " + quantity
                +", price= " + price +"]";
    }

    public Builder toBuilder() {
        return new Builder()
                .withOrder(this.id.getOrder())
                .withProduct(this.id.getProduct())
                .withQuantity(this.quantity)
                .withPrice(this.price);
    }

    public static final class Builder {
        private Order order;
        private Product product;
        private Integer quantity;
        private BigDecimal price;

        private Builder() {
        }

        public Builder withOrder(Order order) {
            this.order = order;
            return this;
        }

        public Builder withProduct(Product product) {
            this.product = product;
            return this;
        }

        public Builder withQuantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}
