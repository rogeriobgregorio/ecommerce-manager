package com.rogeriogregorio.ecommercemanager.dto;

import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import com.rogeriogregorio.ecommercemanager.entities.ProductEntity;
import com.rogeriogregorio.ecommercemanager.entities.primarykey.OrderItemPK;

public class OrderItemRequest {

    private OrderItemPK id = new OrderItemPK();
    private Integer quantity;
    private Double price;

    public OrderItemRequest() {
    }

    public OrderItemRequest(OrderEntity orderEntity, ProductEntity productEntity, Integer quantity, Double price) {

        id.setOrderEntity(orderEntity);
        id.setProductEntity(productEntity);
        this.quantity = quantity;
        this.price = price;
    }

    public OrderItemPK getId() {
        return id;
    }

    public void setId(OrderItemPK id) {
        this.id = id;
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
}
