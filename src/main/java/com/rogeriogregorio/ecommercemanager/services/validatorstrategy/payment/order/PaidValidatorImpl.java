package com.rogeriogregorio.ecommercemanager.services.validatorstrategy.payment.order;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.services.OrderStatusValidator;
import org.springframework.stereotype.Component;

@Component
public class PaidValidatorImpl implements OrderStatusValidator {

    @Override
    public void validate(Order order, OrderRequest orderRequest) {

        OrderStatus statusRequest = orderRequest.getOrderStatus();
        OrderStatus currentStatus = order.getOrderStatus();

        if (currentStatus == OrderStatus.PAID && statusRequest != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Não é possível alterar o status de entrega: o pedido está pago e aguarda o envio.");
        }
    }
}