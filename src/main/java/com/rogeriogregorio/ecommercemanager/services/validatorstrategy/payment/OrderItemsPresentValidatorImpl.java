package com.rogeriogregorio.ecommercemanager.services.validatorstrategy.payment;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
import com.rogeriogregorio.ecommercemanager.services.validatorstrategy.PaymentValidator;
import org.springframework.stereotype.Component;

@Component
public class OrderItemsPresentValidatorImpl implements PaymentValidator {

    private final OrderService orderService;

    public OrderItemsPresentValidatorImpl(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void validate(Order order) {
        if (!orderService.isOrderItemsPresent(order)) {
            throw new IllegalStateException("Não foi possível processar o pagamento: nenhum item no pedido.");
        }
    }
}