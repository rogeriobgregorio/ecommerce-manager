package com.rogeriogregorio.ecommercemanager.services.strategy.payment;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.PaymentStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class DeliveryAddressPresentStrategyImpl implements PaymentStrategy {

    private static final Logger logger = LogManager.getLogger(DeliveryAddressPresentStrategyImpl.class);

    @Override
    public void validate(Order order) {

        boolean isDeliveryAddressEmpty = order.getClient().getAddress() == null;

        if (isDeliveryAddressEmpty) {
            logger.warn("Não foi possível processar o pagamento: endereço de entrega não cadastrado.");
            throw new IllegalStateException("Não foi possível processar o pagamento: endereço de entrega não cadastrado.");
        }
    }
}