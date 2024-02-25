package com.rogeriogregorio.ecommercemanager.services;

import com.rogeriogregorio.ecommercemanager.dto.requests.OrderItemRequest;
import com.rogeriogregorio.ecommercemanager.dto.responses.OrderItemResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface OrderItemService {

    public List<OrderItemResponse> findAllOrderItems();

    public OrderItemResponse createOrderItem(OrderItemRequest orderItemRequest);

    public OrderItemResponse findOrderItemById(Long orderId, Long productId);

    public OrderItemResponse updateOrderItem(OrderItemRequest orderItemRequest);

    public void deleteOrderItem(Long orderId, Long productId);
}
