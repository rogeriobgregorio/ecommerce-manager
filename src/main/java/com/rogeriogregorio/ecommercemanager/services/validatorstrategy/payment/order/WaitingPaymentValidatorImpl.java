package com.rogeriogregorio.ecommercemanager.services.validatorstrategy.payment.order;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.services.OrderStatusValidator;
import org.springframework.stereotype.Component;

@Component
public class WaitingPaymentValidatorImpl implements OrderStatusValidator {

    @Override
    public void validate(Order order, OrderRequest orderRequest) {

        OrderStatus currentStatus = order.getOrderStatus();
        OrderStatus statusRequest = orderRequest.getOrderStatus();

        if (currentStatus == OrderStatus.WAITING_PAYMENT && statusRequest != OrderStatus.CANCELED) {
            throw new IllegalStateException("Não é possível alterar o status de entrega: o pedido está aguardando o pagamento.");
        }
    }
}

