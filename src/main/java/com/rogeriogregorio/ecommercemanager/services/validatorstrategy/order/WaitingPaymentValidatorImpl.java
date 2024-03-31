package com.rogeriogregorio.ecommercemanager.services.validatorstrategy.order;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.services.OrderStatusValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class WaitingPaymentValidatorImpl implements OrderStatusValidator {

    private static final Logger logger = LogManager.getLogger(WaitingPaymentValidatorImpl.class);

    @Override
    public void validate(Order order, OrderRequest orderRequest) {

        OrderStatus currentStatus = order.getOrderStatus();
        OrderStatus statusRequest = orderRequest.getOrderStatus();

        if (currentStatus == OrderStatus.WAITING_PAYMENT && statusRequest != OrderStatus.CANCELED) {
            logger.warn("Não é possível alterar o status de entrega: o pedido {} está aguardando o pagamento.", order);
            throw new IllegalStateException("Não é possível alterar o status de entrega: o pedido está aguardando o pagamento.");
        }
    }
}
