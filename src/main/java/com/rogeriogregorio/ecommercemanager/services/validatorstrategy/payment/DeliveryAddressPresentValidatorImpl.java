package com.rogeriogregorio.ecommercemanager.services.validatorstrategy.payment;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
import com.rogeriogregorio.ecommercemanager.services.validatorstrategy.PaymentValidator;

public class DeliveryAddressPresentValidatorImpl implements PaymentValidator {
    private final OrderService orderService;

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