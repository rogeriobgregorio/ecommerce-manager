package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface OrderService {

    public List<OrderResponse> findAllOrders();

    public OrderResponse createOrder(OrderRequest orderRequest);

    public OrderResponse findOrderById(Long id);

    public OrderResponse updateOrder(OrderRequest orderRequest);

    public void deleteOrder(Long id);

    public List<OrderResponse> findOrderByClientId(Long id);
}
