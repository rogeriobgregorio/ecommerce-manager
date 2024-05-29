package com.rogeriogregorio.ecommercemanager.services.strategy.validations;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import org.springframework.stereotype.Component;

@Component
public interface OrderStatusStrategy {

    void validateStatusChange(OrderRequest orderRequest, Order order);
}
