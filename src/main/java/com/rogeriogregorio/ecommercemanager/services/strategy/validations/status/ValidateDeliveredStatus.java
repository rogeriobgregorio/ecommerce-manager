package com.rogeriogregorio.ecommercemanager.services.strategy.validations.status;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import com.rogeriogregorio.ecommercemanager.entities.enums.OrderStatus;
import com.rogeriogregorio.ecommercemanager.services.strategy.validations.OrderStatusStrategy;
import org.springframework.stereotype.Component;

@Component
public class ValidateDeliveredStatus implements OrderStatusStrategy {

    @Override
    public void validateStatusChange(OrderRequest orderRequest, Order order) {

        OrderStatus statusRequest = orderRequest.getOrderStatus();
        OrderStatus currentStatus = order.getOrderStatus();

        if (currentStatus == OrderStatus.DELIVERED && statusRequest != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Unable to change delivery status: the order is already delivered.");
        }
    }
}
