package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface OrderService {

    public List<OrderResponse> findAllOrders();

    public OrderResponse createOrder(OrderRequest orderRequest);

    public void savePaidOrder(OrderEntity orderEntity);

    public OrderResponse findOrderById(Long id);

    public OrderEntity findOrderEntityById(Long id);

    public OrderResponse updateOrder(OrderRequest orderRequest);

    public void deleteOrder(Long id);

    public List<OrderResponse> findOrderByClientId(Long id);

    public Boolean isOrderPaid(PaymentRequest paymentRequest);
}
