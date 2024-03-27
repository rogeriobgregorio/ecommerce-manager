package com.rogeriogregorio.ecommercemanager.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.Product;
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

    public OrderItemResponse(Order order, Product product, Integer quantity, Double price) {

        id.setOrder(order);
        id.setProduct(product);
        this.quantity = quantity;
        this.price = BigDecimal.valueOf(price);
    }

    public OrderItemResponse(Order order, Product product, Integer quantity, BigDecimal price) {

        id.setOrder(order);
        id.setProduct(product);
        this.quantity = quantity;
        this.price = price;
    }

    @JsonIgnore
    public Order getOrderEntity() {
        return id.getOrder();
    }

    public void setOrderEntity(Order order) {
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

    public void setPrice(Double price) {
        this.price = BigDecimal.valueOf(price);
    }

    public BigDecimal getSubTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
