package com.rogeriogregorio.ecommercemanager.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import com.rogeriogregorio.ecommercemanager.entities.ProductEntity;
import com.rogeriogregorio.ecommercemanager.entities.primarykey.OrderItemPK;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class OrderItemResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private OrderItemPK id = new OrderItemPK();
    private Integer quantity;
    private BigDecimal price;

    public OrderItemResponse() {
    }

    public OrderItemResponse(OrderEntity orderEntity, ProductEntity productEntity, Integer quantity, Double price) {

        id.setOrderEntity(orderEntity);
        id.setProductEntity(productEntity);
        this.quantity = quantity;
        this.price = BigDecimal.valueOf(price);
    }

    public OrderItemResponse(OrderEntity orderEntity, ProductEntity productEntity, Integer quantity, BigDecimal price) {

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
}
