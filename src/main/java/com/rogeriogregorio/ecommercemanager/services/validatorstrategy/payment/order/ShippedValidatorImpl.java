package com.rogeriogregorio.ecommercemanager.services.validatorstrategy.payment.order;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.services.OrderStatusValidator;
import org.springframework.stereotype.Component;

@Component
public class ShippedValidatorImpl implements OrderStatusValidator {

    @Override
    public void validate(Order order, OrderRequest orderRequest) {

        OrderStatus statusRequest = orderRequest.getOrderStatus();
        OrderStatus currentStatus = order.getOrderStatus();

        if (currentStatus == OrderStatus.SHIPPED && statusRequest != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Não é possível alterar o status de entrega: o pedido foi enviado para entrega.");
        }
    }
}