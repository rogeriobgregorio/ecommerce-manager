package com.rogeriogregorio.ecommercemanager.services.validatorstrategy.payment;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.PaymentValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class OrderItemsPresentValidatorImpl implements PaymentValidator {

    private static final Logger logger = LogManager.getLogger(OrderItemsPresentValidatorImpl.class);

    @Override
    public void validate(Order order) {

        boolean isOrderItemsEmpty = order.getItems().isEmpty();

        if (isOrderItemsEmpty) {
            logger.warn("Não foi possível processar o pagamento: nenhum item no pedido.");
            throw new IllegalStateException("Não foi possível processar o pagamento: nenhum item no pedido.");
        }
    }
}