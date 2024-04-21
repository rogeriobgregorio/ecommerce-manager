package com.rogeriogregorio.ecommercemanager.services.strategy.order;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.services.OrderStatusStrategy;
import org.springframework.stereotype.Component;

@Component
public class WaitingPaymentStatusStrategyImpl implements OrderStatusStrategy {

    @Override
    public void validate(OrderRequest orderRequest, Order order) {

        OrderStatus statusRequest = orderRequest.getOrderStatus();
        OrderStatus currentStatus = order.getOrderStatus();

        if (currentStatus == OrderStatus.WAITING_PAYMENT && statusRequest != OrderStatus.CANCELED) {
            throw new IllegalStateException("Unable to change delivery status: the order is awaiting payment.");
        }
    }
}