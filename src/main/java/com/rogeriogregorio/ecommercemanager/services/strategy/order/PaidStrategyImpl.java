package com.rogeriogregorio.ecommercemanager.services.strategy.order;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.services.OrderStatusStrategy;
import org.springframework.stereotype.Component;

@Component
public class PaidStrategyImpl implements OrderStatusStrategy {

    @Override
    public void validate(Order order, OrderRequest orderRequest) {

        OrderStatus statusRequest = orderRequest.getOrderStatus();
        OrderStatus currentStatus = order.getOrderStatus();

        if (currentStatus == OrderStatus.PAID && statusRequest != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Não é possível alterar o status de entrega: o pedido está pago e aguarda ser enviado.");
        }
    }
}