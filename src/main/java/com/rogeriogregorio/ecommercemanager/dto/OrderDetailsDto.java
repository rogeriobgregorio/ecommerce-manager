package com.rogeriogregorio.ecommercemanager.dto;

import com.rogeriogregorio.ecommercemanager.entities.Order;

import java.io.Serial;
import java.io.Serializable;

public class OrderDetailsDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String orderId;
    private String name;
    private String cpf;
    private String items;
    private String amount;

    public OrderDetailsDto() {
    }

    public OrderDetailsDto(Order order) {

        this.orderId = order.getId().toString();
        this.name = order.getClient().getName();
        this.cpf = order.getClient().getCpf();
        this.items = order.getProductQuantities().toString();
        this.amount = order.getTotalFinal().toString();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
