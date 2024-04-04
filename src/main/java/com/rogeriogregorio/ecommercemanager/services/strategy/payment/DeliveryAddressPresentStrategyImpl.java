package com.rogeriogregorio.ecommercemanager.services.strategy.payment;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.PaymentStrategy;
import org.springframework.stereotype.Component;

@Component
public class DeliveryAddressPresentStrategyImpl implements PaymentStrategy {

    @Override
    public void validate(Order order) {

        boolean isDeliveryAddressEmpty = order.getClient().getAddress() == null;

        if (isDeliveryAddressEmpty) {
            throw new IllegalStateException("Não foi possível processar o pagamento: endereço de entrega ausente.");
        }
    }
}