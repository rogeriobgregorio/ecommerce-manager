package com.rogeriogregorio.ecommercemanager.services.strategy;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import org.springframework.stereotype.Component;

@Component
public interface OrderStrategy {

    void validateStatusChange(OrderRequest orderRequest, Order order);
}
