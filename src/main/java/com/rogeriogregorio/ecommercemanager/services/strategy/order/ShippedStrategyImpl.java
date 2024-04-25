package com.rogeriogregorio.ecommercemanager.services.strategy.order;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.services.strategy.OrderStrategy;
import org.springframework.stereotype.Component;

@Component
public class ShippedStrategyImpl implements OrderStrategy {

    @Override
    public void validate(OrderRequest orderRequest, Order order) {

        OrderStatus statusRequest = orderRequest.getOrderStatus();
        OrderStatus currentStatus = order.getOrderStatus();

        if (currentStatus == OrderStatus.SHIPPED && statusRequest != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Unable to change delivery status: the order has been dispatched for delivery.");
        }
    }
}