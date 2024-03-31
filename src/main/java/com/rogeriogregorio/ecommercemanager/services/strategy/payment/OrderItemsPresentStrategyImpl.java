package com.rogeriogregorio.ecommercemanager.services.strategy.payment;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.PaymentStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class OrderItemsPresentStrategyImpl implements PaymentStrategy {

    private static final Logger logger = LogManager.getLogger(OrderItemsPresentStrategyImpl.class);

    @Override
    public void validate(Order order) {

        boolean isOrderItemsEmpty = order.getItems().isEmpty();

        if (isOrderItemsEmpty) {
            logger.warn("Não foi possível processar o pagamento: nenhum item no pedido.");
            throw new IllegalStateException("Não foi possível processar o pagamento: nenhum item no pedido.");
        }
    }
}