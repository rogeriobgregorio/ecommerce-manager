package com.rogeriogregorio.ecommercemanager.dto.requests;

import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class OrderRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer orderStatus;
    private UUID clientId;
    private String discountCouponCode;

    public OrderRequest() {
    }

    public OrderRequest(OrderStatus orderStatus,
                        UUID clientId,
                        String discountCouponCode) {

        setOrderStatus(orderStatus);
        this.clientId = clientId;
        this.discountCouponCode = discountCouponCode;
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

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public String getDiscountCouponCode() {
        return discountCouponCode;
    }

    public void setDiscountCouponCode(String discountCouponCode) {
        this.discountCouponCode = discountCouponCode;
    }
}
