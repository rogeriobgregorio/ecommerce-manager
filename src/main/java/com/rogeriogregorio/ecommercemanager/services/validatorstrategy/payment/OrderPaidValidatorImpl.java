package com.rogeriogregorio.ecommercemanager.services.validatorstrategy.payment;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
import com.rogeriogregorio.ecommercemanager.services.PaymentValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderPaidValidatorImpl implements PaymentValidator {

    private final OrderService orderService;

    @Autowired
    public OrderPaidValidatorImpl(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void validate(Order order) {
        if (orderService.isOrderPaid(order)) {
            throw new IllegalStateException("Não foi possível processar o pagamento: pedido já pago.");
        }
    }
}