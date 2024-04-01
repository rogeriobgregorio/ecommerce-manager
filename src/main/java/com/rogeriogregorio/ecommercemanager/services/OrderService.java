package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface OrderService {

    Page<OrderResponse> findAllOrders(Pageable pageable);

    OrderResponse createOrder(OrderRequest orderRequest);

    void savePaidOrder(Order order);

    OrderResponse findOrderResponseById(Long id);

    Order findOrderById(Long id);

    OrderResponse updateOrder(OrderRequest orderRequest);

    void deleteOrder(Long id);

    Page<OrderResponse> findOrderByClientId(Long id, Pageable pageable);
}
