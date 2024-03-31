package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface OrderService {

    List<OrderResponse> findAllOrders();

    OrderResponse createOrder(OrderRequest orderRequest);

    void savePaidOrder(Order order);

    OrderResponse findOrderResponseById(Long id);

    Order findOrderById(Long id);

    OrderResponse updateOrder(OrderRequest orderRequest);

    void deleteOrder(Long id);

    List<OrderResponse> findOrderByClientId(Long id);
}
