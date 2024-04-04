package com.rogeriogregorio.ecommercemanager.services.strategy.order;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.services.OrderStatusStrategy;
import org.springframework.stereotype.Component;

@Component
public class DeliveredStrategyImpl implements OrderStatusStrategy {

    @Override
    public void validate(Order order, OrderRequest orderRequest) {

        OrderStatus statusRequest = orderRequest.getOrderStatus();
        OrderStatus currentStatus = order.getOrderStatus();

        if (currentStatus == OrderStatus.DELIVERED && statusRequest != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Não é possível alterar o status de entrega: o pedido está entregue.");
        }
    }
}
