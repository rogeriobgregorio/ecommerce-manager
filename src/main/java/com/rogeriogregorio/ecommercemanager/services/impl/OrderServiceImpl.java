package com.rogeriogregorio.ecommercemanager.services.impl;

import com.rogeriogregorio.ecommercemanager.dto.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.OrderResponse;
import com.rogeriogregorio.ecommercemanager.services.OrderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Override
    public List<OrderResponse> findAllOrders() {
        return null;
    }

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) {
        return null;
    }

    @Override
    public OrderResponse findOrderById(Long id) {
        return null;
    }

    @Override
    public OrderResponse updateUser(OrderRequest orderRequest) {
        return null;
    }

    @Override
    public void deleteOrder(Long id) {

    }
}
