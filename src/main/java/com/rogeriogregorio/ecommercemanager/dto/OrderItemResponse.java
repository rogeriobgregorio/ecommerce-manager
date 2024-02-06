package com.rogeriogregorio.ecommercemanager.dto;

import com.rogeriogregorio.ecommercemanager.entities.primarykey.OrderItemPK;

public class OrderItemResponse {

    private OrderItemPK id = new OrderItemPK();
    private Integer quantity;
    private Double price;

    public OrderItemResponse() {
    }

    public OrderItemResponse(OrderItemPK id, Integer quantity, Double price) {
        this.id = id;
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
