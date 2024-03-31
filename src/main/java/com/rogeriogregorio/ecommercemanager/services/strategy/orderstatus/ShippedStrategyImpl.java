package com.rogeriogregorio.ecommercemanager.services.strategy.orderstatus;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.services.OrderStatusStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ShippedStrategyImpl implements OrderStatusStrategy {

    private static final Logger logger = LogManager.getLogger(ShippedStrategyImpl.class);

    @Override
    public void validate(Order order, OrderRequest orderRequest) {

        OrderStatus statusRequest = orderRequest.getOrderStatus();
        OrderStatus currentStatus = order.getOrderStatus();

        if (currentStatus == OrderStatus.SHIPPED && statusRequest != OrderStatus.DELIVERED) {
            logger.warn("Não é possível alterar o status de entrega: o pedido {} foi enviado para entrega.", order);
            throw new IllegalStateException("Não é possível alterar o status de entrega: o pedido foi enviado para entrega.");
        }
    }
}