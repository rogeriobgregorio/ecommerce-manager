package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderRequest;
import com.rogeriogregorio.ecommercemanager.dto.requests.PaymentRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderResponse;
import com.rogeriogregorio.ecommercemanager.entities.OrderEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface OrderService {

    List<OrderResponse> findAllOrders();

    OrderResponse createOrder(OrderRequest orderRequest);

    void savePaidOrder(OrderEntity orderEntity);

    OrderResponse findOrderById(Long id);

    OrderEntity findOrderEntityById(Long id);

    OrderResponse updateOrder(OrderRequest orderRequest);

    void deleteOrder(Long id);

    List<OrderResponse> findOrderByClientId(Long id);

    Boolean isOrderPaid(PaymentRequest paymentRequest);
}
