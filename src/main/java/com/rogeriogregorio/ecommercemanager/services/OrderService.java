package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.OrderResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface OrderService {

    public List<OrderResponse> findAllOrders();

    public OrderResponse createOrder(OrderRequest orderRequest);

    public OrderResponse findOrderById(Long id);

    public OrderResponse updateUser(OrderRequest orderRequest);

    public void deleteOrder(Long id);
}
