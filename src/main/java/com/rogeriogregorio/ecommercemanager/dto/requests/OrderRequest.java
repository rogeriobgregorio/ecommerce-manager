package com.rogeriogregorio.ecommercemanager.dto.requests;

import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;

import java.io.Serial;
import java.io.Serializable;

public class OrderRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer orderStatus;
    private Long clientId;
    private String discountCouponCode;

    public OrderRequest() {
    }

    public OrderRequest(Long clientId) {
        this.clientId = clientId;
    }

    public OrderRequest(Long id, OrderStatus orderStatus) {
        this.id = id;
        setOrderStatus(orderStatus);
    }

    public OrderRequest(Long id, OrderStatus orderStatus, Long clientId, String discountCouponCode) {
        this.id = id;
        setOrderStatus(orderStatus);
        this.clientId = clientId;
        this.discountCouponCode = discountCouponCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getOrderStatus() {
        return OrderStatus.valueOf(orderStatus);
    }

    public void setOrderStatus(OrderStatus orderStatus) {

        if (orderStatus == null) {
            throw new IllegalArgumentException("The order status cannot be null");
        }

        this.orderStatus = orderStatus.getCode();
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getDiscountCouponCode() {
        return discountCouponCode;
    }

    public void setDiscountCouponCode(String discountCouponCode) {
        this.discountCouponCode = discountCouponCode;
    }
}
