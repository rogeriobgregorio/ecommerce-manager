package com.rogeriogregorio.ecommercemanager.services.validatorstrategy.payment;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
import com.rogeriogregorio.ecommercemanager.services.PaymentValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeliveryAddressPresentValidatorImpl implements PaymentValidator {

    private final OrderService orderService;

    @Autowired
    public DeliveryAddressPresentValidatorImpl(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void validate(Order order) {
        if (!orderService.isDeliveryAddressPresent(order)) {
            throw new IllegalStateException("Não foi possível processar o pagamento: endereço de entrega não cadastrado.");
        }
    }
}