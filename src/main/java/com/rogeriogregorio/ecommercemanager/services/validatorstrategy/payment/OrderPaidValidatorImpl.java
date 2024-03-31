package com.rogeriogregorio.ecommercemanager.services.validatorstrategy.payment;

import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.services.PaymentValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class OrderPaidValidatorImpl implements PaymentValidator {

    private static final Logger logger = LogManager.getLogger(OrderPaidValidatorImpl.class);

    @Override
    public void validate(Order order) {

        String orderStatus = order.getOrderStatus().name();
        boolean isOrderPaid = Set.of("PAID", "SHIPPED", "DELIVERED").contains(orderStatus);

        if (isOrderPaid) {
            logger.warn("Não foi possível processar o pagamento: o pedido já está pago.");
            throw new IllegalStateException("Não foi possível processar o pagamento: o pedido já está pago.");
        }
    }
}