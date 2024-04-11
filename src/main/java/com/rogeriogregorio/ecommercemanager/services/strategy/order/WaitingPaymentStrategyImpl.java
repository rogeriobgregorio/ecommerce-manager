package com.rogeriogregorio.ecommercemanager.services.strategy.order;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.services.OrderStatusStrategy;
import org.springframework.stereotype.Component;

@Component
public class WaitingPaymentStrategyImpl implements OrderStatusStrategy {

    @Override
    public void validate(Order order, OrderRequest orderRequest) {

        OrderStatus currentStatus = order.getOrderStatus();
        OrderStatus statusRequest = orderRequest.getOrderStatus();

        if (currentStatus == OrderStatus.WAITING_PAYMENT && statusRequest != OrderStatus.CANCELED) {
            throw new IllegalStateException("Unable to change delivery status: the order is awaiting payment.");
        }
    }
}

