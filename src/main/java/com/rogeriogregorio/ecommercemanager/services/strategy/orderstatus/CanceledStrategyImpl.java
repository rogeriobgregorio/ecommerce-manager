package com.rogeriogregorio.ecommercemanager.services.strategy.orderstatus;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.services.OrderStatusStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class CanceledStrategyImpl implements OrderStatusStrategy {

    private static final Logger logger = LogManager.getLogger(CanceledStrategyImpl.class);

    @Override
    public void validate(Order order, OrderRequest orderRequest) {

        OrderStatus statusRequest = orderRequest.getOrderStatus();
        OrderStatus currentStatus = order.getOrderStatus();

        if (currentStatus == OrderStatus.CANCELED && statusRequest != OrderStatus.CANCELED) {
            logger.warn("Não é possível alterar o status de entrega: o pedido {} foi cancelado.", order);
            throw new IllegalStateException("Não é possível alterar o status de entrega: o pedido foi cancelado.");
        }
    }
}
