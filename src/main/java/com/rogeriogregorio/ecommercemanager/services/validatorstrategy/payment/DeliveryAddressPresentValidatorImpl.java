package com.rogeriogregorio.ecommercemanager.services.validatorstrategy.payment;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.PaymentValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class DeliveryAddressPresentValidatorImpl implements PaymentValidator {

    private static final Logger logger = LogManager.getLogger(DeliveryAddressPresentValidatorImpl.class);

    @Override
    public void validate(Order order) {

        boolean isDeliveryAddressEmpty = order.getClient().getAddress() == null;

        if (isDeliveryAddressEmpty) {
            logger.warn("Não foi possível processar o pagamento: endereço de entrega não cadastrado.");
            throw new IllegalStateException("Não foi possível processar o pagamento: endereço de entrega não cadastrado.");
        }
    }
}